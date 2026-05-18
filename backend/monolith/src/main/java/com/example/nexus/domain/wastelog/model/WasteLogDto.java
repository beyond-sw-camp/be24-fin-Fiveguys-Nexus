package com.example.nexus.domain.wastelog.model;

import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WasteReq {
        private Long storeInventoryIdx;

        @Min(value = 1, message = "폐기 수량은 1 이상이어야 합니다.")
        private Integer quantity;

        @NotBlank(message = "폐기 사유를 입력해주세요.")
        private String wasteReason;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PosWasteReq {
        @NotNull(message = "POS 재고 lot id는 필수입니다.")
        private Long posStoreInventoryIdx;

        @Min(value = 1, message = "폐기 수량은 1 이상이어야 합니다.")
        private Integer quantity;

        @NotBlank(message = "폐기 사유를 입력해주세요.")
        private String wasteReason;
    }

    @Getter
    @Builder
    public static class WasteRes {
        private Long wasteLogIdx;
        private Long storeIdx;
        private String storeName;
        private Long productIdx;
        private String productName;
        private Integer quantity;
        private Long amountLoss;
        private LocalDateTime wasteDate;
        private String wasteReason;

        public static WasteRes from(WasteLog entity) {
            return WasteRes.builder()
                    .wasteLogIdx(entity.getIdx())
                    .storeIdx(entity.getStore().getIdx())
                    .storeName(entity.getStore().getStoreName())
                    .productIdx(entity.getProduct().getIdx())
                    .productName(entity.getProduct().getProductName())
                    .quantity(entity.getQuantity())
                    .amountLoss(Long.valueOf(entity.getAmountLoss()))
                    .wasteDate(entity.getWasteDate())
                    .wasteReason(entity.getWasteReason())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class StatsRes {
        private Long wasteSum;
        private Float wasteGradient;
        private Float useSuccessRate;
    }
}
