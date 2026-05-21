package com.example.nexus.domain.billing.model;

import lombok.*;

import java.util.List;

public class BillingDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IssueBillingKeyRequestDto {
        private String authKey;
        private String customerKey;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingResponseDto {
        private Long id;
        private String mId;
        private String customerKey;
        private String authenticatedAt;
        private String method;
        private Long storeIdx;

        public static BillingResponseDto from(Billing billing) {
            return BillingResponseDto.builder()
                    .id(billing.getId())
                    .mId(billing.getMId())
                    .customerKey(billing.getCustomerKey())
                    .authenticatedAt(billing.getAuthenticatedAt())
                    .method(billing.getMethod())
                    .storeIdx(billing.getStoreIdx())
                    .build();
        }
    }
}
