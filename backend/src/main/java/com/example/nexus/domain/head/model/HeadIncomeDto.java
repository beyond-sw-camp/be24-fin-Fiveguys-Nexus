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
        private Long settlementIdx;
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
}
