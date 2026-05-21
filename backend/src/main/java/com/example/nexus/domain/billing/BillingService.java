package com.example.nexus.domain.billing;

import com.example.nexus.common.exception.BaseException;
import com.example.nexus.common.model.BaseResponseStatus;
import com.example.nexus.domain.billing.model.Billing;
import com.example.nexus.domain.billing.model.BillingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BillingService {

    private final BillingRepository billingRepository;
    private final RestClient restClient = RestClient.create();

    @Value("${billing.secret-key}")
    private String secretKey;

    @Transactional
    public void issueBillingKey(Long storeIdx, BillingDto.IssueBillingKeyRequestDto request) {
        String basicAuthHeader = "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());

        // 프론트에서 넘어온 customerKey 대신 세션의 storeIdx를 사용하여 Toss API 호출
        // (Toss는 요청 시 사용한 customerKey와 동일한 값을 보내야 함)
        String customerKey = "STORE_" + storeIdx;

        try {
            Map<?, ?> responseBody = restClient.post()
                    .uri("https://api.tosspayments.com/v1/billing/authorizations/issue")
                    .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "authKey", request.getAuthKey(),
                            "customerKey", customerKey
                    ))
                    .retrieve()
                    .body(Map.class);

            if (responseBody == null) {
                throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
            }

            // 1. 이미 해당 가맹점의 빌링 정보가 있는지 확인 (중복 체크)
            billingRepository.findByStoreIdx(storeIdx).ifPresentOrElse(
                // 2-1. 존재하면 기존 정보를 업데이트 (Dirty Checking 활용)
                existingBilling -> existingBilling.updateBillingInfo(
                    responseBody.get("mId").toString(),
                    responseBody.get("customerKey").toString(),
                    responseBody.get("authenticatedAt").toString(),
                    responseBody.get("method").toString(),
                    responseBody.get("billingKey").toString()
                ),
                // 2-2. 존재하지 않으면 새로 생성하여 저장
                () -> {
                    Billing newBilling = Billing.builder()
                        .mId(responseBody.get("mId").toString())
                        .customerKey(responseBody.get("customerKey").toString())
                        .authenticatedAt(responseBody.get("authenticatedAt").toString())
                        .method(responseBody.get("method").toString())
                        .billingKey(responseBody.get("billingKey").toString())
                        .storeIdx(storeIdx)
                        .build();
                    billingRepository.save(newBilling);
                }
            );

        } catch (Exception e) {
            throw new RuntimeException("빌링키 발급 중 오류 발생: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<BillingDto.BillingResponseDto> getBillingListByStore(Long storeIdx) {
        return billingRepository.findByStoreIdx(storeIdx).stream()
                .map(BillingDto.BillingResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BillingDto.BillingResponseDto> getBillingList() {
        return billingRepository.findAll().stream()
                .map(BillingDto.BillingResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBillingInfo(Long storeIdx) {
        billingRepository.deleteByStoreIdx(storeIdx);
    }
}
