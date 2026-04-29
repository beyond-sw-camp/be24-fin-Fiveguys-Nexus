package com.example.nexus.domain.store.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class StoreDto {

    @Builder
    @Getter
    // 가맹점 리스트 조회
    public static class StoreListRes{
        private Long idx;
        private String storeName;
        private String ownerName;
        private String ownerEmail;
        private String address;
        private String business;
        private LocalDateTime createdAt;
        private LocalDateTime closedAt;


        public static StoreDto.StoreListRes from(Store entity){
            return StoreListRes.builder()
                    .idx(entity.getIdx())
                    .storeName(entity.getStoreName())
                    .ownerName(entity.getUser().getUserName())
                    .ownerEmail(entity.getUser().getEmail())
                    .address(entity.getAddress())
                    .business(entity.getBusiness())
                    .createdAt(entity.getCreatedAt())
                    .closedAt(entity.getClosedAt())
                    .build();
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreSearchReq {
        private String keyword;
    }

    @Builder
    @Getter
    public static class StoreSearchRes {
        private Long idx;
        private String storeName;
        private String address;

        public static StoreSearchRes from(Store entity) {
            return StoreSearchRes.builder()
                    .idx(entity.getIdx())
                    .storeName(entity.getStoreName())
                    .address(entity.getAddress())
                    .build();
        }
    }
}
