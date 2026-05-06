package com.example.nexus.domain.settlement.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class SettlementDto {

    @Getter
    @Builder
    public static class PayReq {
        private Integer paymentPrice;
        private List<Long> orderIdxList;
    }

    @Getter
    @Builder
    public static class VerifyReq {
        private String paymentIdx;
    }





}
