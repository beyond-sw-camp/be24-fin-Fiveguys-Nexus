package com.example.nexus.domain.pos.model;

import com.example.nexus.common.enums.InventoryStatus;
import com.example.nexus.domain.store.model.StoreInventory;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class PosStoreInventoryDto {
    @Getter
    @Builder
    public static class ListRes {
        private Long idx;
        private Integer count;
        private InventoryStatus status;
        private LocalDateTime manufacturedDate;
        private Long storeIdx;
        private String storeName;
        private Long productIdx;
        private String productName;
        private Integer minStock;

        public static PosStoreInventoryDto.ListRes from(PosStoreInventory entity) {
            return PosStoreInventoryDto.ListRes.builder()
                    .idx(entity.getIdx())
                    .count(entity.getCount())
                    .status(entity.getStatus())
                    .manufacturedDate(entity.getManufacturedDate())
                    .storeIdx(entity.getStore().getIdx())
                    .storeName(entity.getStore().getStoreName())
                    .productIdx(entity.getProduct().getIdx())
                    .productName(entity.getProduct().getName())
                    .minStock(entity.getProduct().getMinStock())
                    .build();
        }
    }
}
