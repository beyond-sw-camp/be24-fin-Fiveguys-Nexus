package com.example.nexus.domain.head.model;

import lombok.Builder;
import lombok.Getter;

public class HeadIncomeDto {

    @Builder
    @Getter
    public static class PayReq {
        private Long price;
        private int ordersIdx;
    }

}
