package org.example.spring.billingbatch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.example.spring.billingbatch.model.AfterBilling;
import org.example.spring.billingbatch.model.Billing;
import org.example.spring.billingbatch.model.Store;
import org.example.spring.billingbatch.repository.AfterBillingRepository;
import org.example.spring.billingbatch.repository.BillingRepository;
import org.example.spring.billingbatch.repository.StoreRepository;
import org.example.spring.billingbatch.service.OrdersService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Configuration
public class BillingBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final StoreRepository storeRepository;
    private final BillingRepository billingRepository;
    private final OrdersService ordersService;
    private final AfterBillingRepository afterBillingRepository;
    private final EntityManagerFactory entityManagerFactory;

    @Value("${billing.secret-key}")
    private String secretKey;

    // Processor에서 Writer로 데이터를 전달하기 위한 DTO
    public record BillingRequestDto(
            Long storeIdx,
            int totalPayAmount,
            String customerKey,
            String billingKey,
            boolean hasBillingInfo // 결제 정보 존재 여부 추가
    ) {}

    @Bean
    public Job settlementJob() {
        return new JobBuilder("billingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(billingStep())
                .build();
    }

    @Bean
    public Step billingStep() {
        return new StepBuilder("billingStep", jobRepository)
                .<Store, BillingRequestDto>chunk(5, platformTransactionManager)
                .reader(storeIdReader())
                .processor(billingProcessor())
                .writer(billingWriter(afterBillingRepository))
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(100)
                .build();
    }

    @Bean
    public ItemReader<Store> storeIdReader() {
        RepositoryItemReader<Store> delegate =
                new RepositoryItemReaderBuilder<Store>()
                        .name("storeIdReader")
                        .pageSize(5)
                        .methodName("findAll")
                        .repository(storeRepository)
                        .sorts(Map.of("idx", Sort.Direction.ASC))
                        .build();

        return () -> {
            Store store = delegate.read();
            System.out.println("READER READ = " + (store != null ? store.getIdx() : "NULL"));
            return store;
        };
    }

    @Bean
    public ItemProcessor<Store, BillingRequestDto> billingProcessor() {
        return store -> {
            Long storeIdx = store.getIdx();
            int totalPayAmount = ordersService.totalPayAmount(storeIdx);
            Billing target = billingRepository.findByStoreIdx(storeIdx);

            // 결제 정보가 없더라도 null을 반환하지 않고 DTO를 생성하여 Writer로 넘김
            if (target == null) {
                return new BillingRequestDto(storeIdx, totalPayAmount, null, null, false);
            }

            return new BillingRequestDto(
                    storeIdx,
                    totalPayAmount,
                    target.getCustomerKey(),
                    target.getBillingKey(),
                    true
            );
        };
    }

    @Bean
    public ItemWriter<BillingRequestDto> billingWriter(AfterBillingRepository afterBillingRepository) {
        return new ItemWriter<BillingRequestDto>() {
            private final RestTemplate restTemplate = new RestTemplate();
            private final HttpHeaders headers = new HttpHeaders();

            {
                String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
                headers.set("Authorization", "Basic " + encodedAuth);
                headers.setContentType(MediaType.APPLICATION_JSON);
            }

            @Override
            public void write(@NonNull Chunk<? extends BillingRequestDto> chunk) throws Exception {
                for (BillingRequestDto req : chunk) {
                    AfterBilling afterBilling = AfterBilling.builder()
                            .storeIdx(req.storeIdx())
                            .totalPayAmount(req.totalPayAmount())
                            .payedMonth(YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))
                            .createdAt(LocalDateTime.now())
                            .build();

                    // 1. 결제 정보가 없는 경우 처리
                    if (!req.hasBillingInfo()) {
                        afterBilling.setIsPaid(false);
                        afterBilling.setIsSuccess(false);
                        afterBilling.setFailReason("가맹점 결제 정보(Billing)가 존재하지 않습니다.");
                        afterBillingRepository.save(afterBilling);
                        continue;
                    }

                    try {
                        // 2. 금액이 0원 이하인 경우 (선택 사항)
                        if (req.totalPayAmount() <= 0) {
                            afterBilling.setIsPaid(false);
                            afterBilling.setIsSuccess(false);
                            afterBilling.setFailReason("결제할 금액이 0원 이하입니다.");
                            afterBillingRepository.save(afterBilling);
                            continue;
                        }

                        // 3. 결제 API 호출
                        Map<String, Object> map = new HashMap<>();
                        map.put("customerKey", req.customerKey());
                        map.put("amount", req.totalPayAmount());
                        map.put("orderId", UUID.randomUUID().toString());
                        map.put("orderName", "지난 달 정산 결제");

                        HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
                        String url = "https://api.tosspayments.com/v1/billing/" + req.billingKey();

                        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                        
                        // 성공 기록
                        afterBilling.setIsPaid(true);
                        afterBilling.setIsSuccess(true);
                        System.out.println("결제 성공: " + req.storeIdx());

                    } catch (Exception e) {
                        // 4. API 호출 실패 처리
                        System.err.println("결제 API 에러: " + req.storeIdx() + " - " + e.getMessage());
                        afterBilling.setIsPaid(false);
                        afterBilling.setIsSuccess(false);
                        afterBilling.setFailReason("API 호출 실패: " + e.getMessage());
                    } finally {
                        // 최종 저장
                        afterBillingRepository.save(afterBilling);
                    }
                }
            }
        };
    }
}