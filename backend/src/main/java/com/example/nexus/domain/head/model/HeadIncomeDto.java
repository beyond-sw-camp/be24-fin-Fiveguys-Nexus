package com.example.nexus.domain.head.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class HeadIncomeDto {

    @Builder
    @Getter
    public static class UnpaidRes {
        private Long idx;
        private Boolean status;
        private Long price;
        private Long storeIdx;
    }

    @Getter
    public static class FindHeadIncomeReq {
        private Long orderIdx;
    }

    @Getter
    @Builder
    public static class FindHeadIncomeRes {
        private Long idx;
        private Long price;
        private boolean paid;
        private Long storeIdx;
        private Long ordersIdx;
    }

    @Getter
    @Builder
    public static class ListRes {
        private Long headIncomeIdx;
        private Long storeIdx;
        private String storeName;
        private LocalDate periodStart;
        private LocalDate periodEnd;
        private int orderCount;
        private Long totalPrice;
        private Boolean status;
        private Long settlementIdx;
    }

    // 본사 정산 관리 - 가맹점별 정산 요약 단건
    @Getter
    @Builder
    public static class StoreSettlementRes {
        private Long storeIdx;
        private String storeName;
        private LocalDate periodStart;
        private LocalDate periodEnd;
        private int orderCount;
        private Long totalPrice;
        private Boolean status;
    }

    // 본사 정산 관리 - 전체 응답 (상단 요약 카드 + 페이징 리스트)
    @Getter
    @Builder
    public static class HeadSettlementSummaryRes {
        private Long totalBillingAmount;
        private int totalStoreCount;
    }
}
