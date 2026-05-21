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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
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
import java.util.*;

@RequiredArgsConstructor
@Configuration
public class BillingBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final StoreRepository storeRepository;
    private final BillingRepository billingRepository; // 추가
    private final OrdersService ordersService;
    private final AfterBillingRepository afterBillingRepository;
    private final EntityManagerFactory entityManagerFactory;

    @Value("${billing.secret-key}")
    private String secretKey;
//    YearMonth yearMonth = YearMonth.from(LocalDateTime.now());
    int count = 0;
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
                .<Store, AfterBilling>chunk(5, platformTransactionManager)
                .reader(storeIdReader())
                .processor(billingProcessor())
                .writer(billingWriter(afterBillingRepository))
                .faultTolerant() // 예외 처리 활성화
                .skip(Exception.class) // API 실패 등의 예외 발생 시 해당 아이템만 건너뜀
                .skipLimit(100) // 최대 건너뛸 횟수 지정
                .build();
    }


//    @Bean
//    public JpaCursorItemReader<Store> storeIdReader() {
//        return new JpaCursorItemReaderBuilder<Store>()
//                .name("storeIdReader")
//                .entityManagerFactory(entityManagerFactory)
//                .queryString("SELECT s FROM Store s ORDER BY s.idx ASC")
//                .build();
//    }


//    @Bean
//    public RepositoryItemReader<Store> storeIdReader() {
//        RepositoryItemReader<Store> reader = new RepositoryItemReaderBuilder<Store>()
//                .name("storeIdReader")
//                .pageSize(5)
//                .methodName("findAll")
//                .repository(storeRepository)
//                .sorts(Map.of("idx", Sort.Direction.ASC))
//                .build();
//        return reader;
//    }


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

            System.out.println(
                    "READER READ = " +
                            (store != null ? store.getIdx() : "NULL")
            );

            return store;
        };
    }



    int count2 = 0;

    @Bean
    public ItemProcessor<Store, AfterBilling> billingProcessor() {
        count ++;
        System.out.println(count + " 번째 Processor 실행");
        return new ItemProcessor<Store, AfterBilling>() {

            // 토스 API용 공통 헤더 생성 (프로세서 초기화 시 딱 한 번만 수행)
            private final RestTemplate restTemplate = new RestTemplate();
            private final HttpHeaders headers = new HttpHeaders();

            {
                String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
                headers.set("Authorization", "Basic " + encodedAuth);
                headers.setContentType(MediaType.APPLICATION_JSON);
            }

            @Override
            public AfterBilling process(@NonNull Store store) throws Exception {
                Long storeIdx = store.getIdx();
                count2++;
                System.out.println(storeIdx);
                System.out.println(count2 + " 번째 process 실행");
                // 1. 각 가맹점의 한 달 총 발주 금액 구함
                int totalPayAmount = ordersService.totalPayAmount(storeIdx);

                // 금액이 0원이면 결제를 넘기는 예외 처리 추가 가능 (예: return null;)
//                if (totalPayAmount <= 0) {
//                    return null; // null을 반환하면 해당 아이템은 Writer로 넘어가지 않음.
//                }

                // 2. 가맹점 결제(Billing) 정보 조회
                Billing target = billingRepository.findByStoreIdx(storeIdx);
                if (target == null) {
                    System.err.println("결제 정보가 없는 가맹점 코드: " + storeIdx);
                    return null;
                }
                System.out.println("billingKey-------------------------------" + target.getBillingKey());

                try {
                    // 3. 토스페이먼츠 API 요청 Body 생성
                    Map<String, Object> map = new HashMap<>();
                    map.put("customerKey", target.getCustomerKey());
                    map.put("amount", totalPayAmount);
                    map.put("orderId", UUID.randomUUID().toString());
                    map.put("orderName", "지난 달 결제");

                    HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
                    String url = "https://api.tosspayments.com/v1/billing/" + target.getBillingKey();

                    // 4. 결제 API 호출
                    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                    System.out.println("결제 성공: " + response.getBody());

                    // 5. 성공 결과를 기록할 엔티티 생성 후 반환 (Writer로 전달됨)
                    return AfterBilling.builder()
                            .storeIdx(storeIdx)
                            .totalPayAmount(totalPayAmount)
                            .isPaid(true)
//                            .responseBody(response.getBody())
                            .build();

                } catch (Exception e) {
                    System.err.println("결제 실패 - 유저: " + target.getCustomerKey() + ", 사유: " + e.getMessage());
                    // 프로세서 내부에서 예외를 던져 Step의 skip 메커니즘을 타게 하거나,
                    // 혹은 실패 이력 전용 엔티티를 만들어 Writer로 넘겨 저장할 수도 있습니다.
                    throw e;
                }
            }
        };
    }

    @Bean
    public RepositoryItemWriter<AfterBilling> billingWriter(AfterBillingRepository afterBillingRepository) {
        // SettlementRepository를 이용해 가공 완료된 AfterBilling 객체들을 일괄 저장합니다.
        return new RepositoryItemWriterBuilder<AfterBilling>()
                .repository(afterBillingRepository)
                .methodName("save")
                .build();
    }
}