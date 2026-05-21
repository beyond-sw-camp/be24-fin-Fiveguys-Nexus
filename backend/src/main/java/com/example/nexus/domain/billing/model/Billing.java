package com.example.nexus.domain.billing.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Billing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mId; // 상점 ID, 토스페이먼츠에서 발급
    private String customerKey; // 구매자 ID, 빌링키와 연결
    private String authenticatedAt; // 등록 시간
    private String method; // 결제 수단
    private String billingKey;
    private Long storeIdx;

    public void updateBillingInfo(String mId, String customerKey, String authenticatedAt, String method, String billingKey) {
        this.mId = mId;
        this.customerKey = customerKey;
        this.authenticatedAt = authenticatedAt;
        this.method = method;
        this.billingKey = billingKey;
    }
}
