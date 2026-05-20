package com.example.nexus.domain.billing;

import com.example.nexus.domain.billing.model.BillingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/billing")
public class BillingController {

    // 토스페이먼츠에서 발급받은 시크릿 키 (실무에서는 application.yml 등 환경변수로 관리하세요)
    @Value("${billing.secret-key}")
    private String SECRET_KEY;

    private final BillingService billingService;
    private final RestClient restClient = RestClient.create();

    @PostMapping("/issue")
    public ResponseEntity<?> issueBillingKey(@RequestBody BillingDto.BillingKeyReqDto request) {
        try {
            // 시크릿 키 뒤에 ':'을 붙이고 Base64 인코딩 (Basic Auth)
            String basicAuthHeader = "Basic " + Base64.getEncoder()
                    .encodeToString((SECRET_KEY + ":").getBytes());

            // 토스페이먼츠 API 호출
            Map<?, ?> responseBody = restClient.post()
                    .uri("https://api.tosspayments.com/v1/billing/authorizations/issue")
                    .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "authKey", request.getAuthKey(),
                            "customerKey", request.getCustomerKey()
                    ))
                    .retrieve()
                    .body(Map.class);


            BillingDto.UserReqDto userReqDto = BillingDto.UserReqDto.builder()
                    .mId(responseBody.get("mId").toString())
                    .customerKey(responseBody.get("customerKey").toString())
                    .authenticatedAt(responseBody.get("authenticatedAt").toString())
                    .method(responseBody.get("method").toString())
                    .billingKey(responseBody.get("billingKey").toString())
                    .build();

            billingService.saveUserBillingInfo(userReqDto);

            // 성공 시 토스에서 내려준 빌링키 정보 반환
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            // 에러 발생 시 상태코드와 함께 에러 메시지 반환
            return ResponseEntity.status(500)
                    .body(Map.of("code", "SERVER_ERROR", "message", e.getMessage()));
        }
    }
}
