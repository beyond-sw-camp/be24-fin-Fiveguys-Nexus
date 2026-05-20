package com.example.nexus.domain.billing.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class BillingDto {
    @Builder
    @Getter
    public static class UserReqDto {
        private String mId; // 상점 ID, 토스페이먼츠에서 발급
        private String customerKey; // 구매자 ID, 빌링키와 연결
        private String authenticatedAt;
        private String method;  // 결제 수단
        private String billingKey;  // 정기 결제 카드 등록 시 발급
        private Long storeIdx;  // 본인 가맹점

        public Billing toEntity() {
            return Billing.builder()
                    .mId(mId)
                    .customerKey(customerKey)
                    .authenticatedAt(authenticatedAt)
                    .method(method)
                    .billingKey(billingKey)
                    .storeIdx(storeIdx)
                    .build();
        }

    }


    @Getter @Setter
    public static class BillingKeyReqDto {
        private String authKey;   //
        private String customerKey;     //
    }

}
