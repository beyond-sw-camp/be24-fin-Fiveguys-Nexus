package com.example.nexus.domain.settlement.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class SettlementDto {

    @Getter
    @Builder
    public static class PayReq {
        private Long paymentPrice;
        List<Long> headIncomeidxList;
    }

    @Getter
    @Builder
    public static class VerifyReq {
        private String paymentIdx;
    }





}
