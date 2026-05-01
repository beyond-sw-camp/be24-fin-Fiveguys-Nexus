package com.example.nexus.domain.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    }
}
