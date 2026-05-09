package com.example.nexus.domain.wastelog.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class WasteLogDto {

    @Builder
    @Getter
    public static class RegRes {
        private Long idx;
        private int amountLoss;
        private int quantity;
        private LocalDateTime wasteDate;
        private String wasteReason;
        private Long productIdx;
        private Long storeIdx;

        public static WasteLogDto.RegRes from (WasteLog entity) {
            return RegRes.builder()
                    .idx(entity.getIdx())
                    .amountLoss(entity.getAmountLoss())
                    .quantity(entity.getQuantity())
                    .wasteDate(entity.getWasteDate())
                    .wasteReason(entity.getWasteReason())
                    .productIdx(entity.getProduct().getIdx())
                    .storeIdx(entity.getStore().getIdx())
                    .build();
        }

    }

}
