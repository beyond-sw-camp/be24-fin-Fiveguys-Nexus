package com.example.nexus.domain.settlement.model;

import com.example.nexus.domain.head.model.HeadIncomeDto;
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
    public static class PayRes {
        private Long settlementIdx;
        private Long paymentPrice;
        private List<Long> headIncomeIdxList;
    }

    @Getter
    @Builder
    public static class VerifyReq {
        private String paymentId;
    }

    @Getter
    @Builder
    public static class unPaid {
        private List<HeadIncomeDto.FindHeadIncomeRes> unpaidList;
        private Long settlementIdx;
    }


}
