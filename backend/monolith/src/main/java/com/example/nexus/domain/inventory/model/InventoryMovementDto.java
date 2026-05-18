package com.example.nexus.domain.inventory.model;

import com.example.nexus.common.enums.MovementLocationType;
import com.example.nexus.common.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovementListRes {
        private Long movementIdx;
        private Long productIdx;
        private String productName;
        private MovementType movementType;
        private Integer quantity;
        private MovementLocationType fromLocationType;
        private Long fromRefIdx;
        private MovementLocationType toLocationType;
        private Long toRefIdx;
        private String memo;
        private LocalDateTime createdAt;

        public static MovementListRes from(InventoryMovement movement) {
            return MovementListRes.builder()
                    .movementIdx(movement.getIdx())
                    .productIdx(movement.getProduct().getIdx())
                    .productName(movement.getProduct().getProductName())
                    .movementType(movement.getMovementType())
                    .quantity(movement.getQuantity())
                    .fromLocationType(movement.getFromLocationType())
                    .fromRefIdx(movement.getFromRefIdx())
                    .toLocationType(movement.getToLocationType())
                    .toRefIdx(movement.getToRefIdx())
                    .memo(movement.getMemo())
                    .createdAt(movement.getCreatedAt())
                    .build();
        }
    }
}
