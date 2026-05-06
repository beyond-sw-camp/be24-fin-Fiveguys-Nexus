package com.example.nexus.domain.settlement.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class SettlementDto {

    @Getter
    @Builder
    public static class PayReq {
        @Setter
        private Long storeIdx;
        private int month;
    }

    @Builder
    @Getter
    public static class PayRes {
        private Long price;
        private boolean status;
        private String pgPaymentId;
        private Long storeIdx;
        private Long ordersIdx;
    }


}
