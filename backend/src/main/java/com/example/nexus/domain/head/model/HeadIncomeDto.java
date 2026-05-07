package com.example.nexus.domain.head.model;

import lombok.Builder;
import lombok.Getter;

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

}
