package com.example.nexus.domain.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class InventoryMovementDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InboundReq {
        private Long productIdx;
        private Integer quantity;
        private String memo;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutboundReq {
        private Long productIdx;
        private Long storeIdx;
        private Integer quantity;
        private String memo;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovementRes {
        private Long movementIdx;
        private Long productIdx;
        private Integer quantity;
        private Integer headCount;
        private Long storeIdx;
        private Integer storeCount;

        public static MovementRes from(InventoryMovement movement) {
            return MovementRes.builder()
                    .movementIdx(movement.getIdx())
                    .productIdx(movement.getProduct().getIdx())
                    .quantity(movement.getQuantity())
                    .storeIdx(movement.getToRefIdx())
                    .build();
        }
    }
}
