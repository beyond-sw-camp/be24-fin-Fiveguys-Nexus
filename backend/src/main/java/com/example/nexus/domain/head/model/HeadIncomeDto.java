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

}
