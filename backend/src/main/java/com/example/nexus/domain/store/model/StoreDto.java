package com.example.nexus.domain.store.model;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        private String status;


        public static StoreDto.StoreListRes from(Store entity){
            return StoreListRes.builder()
                    .idx(entity.getIdx())
                    .storeName(entity.getStoreName())
                    .ownerName(entity.getUser().getUserName())
                    .ownerEmail(entity.getUser().getEmail())
                    .address(entity.getAddress())
                    .business(entity.getBusiness())
                    .status(entity.isDeleted() ? "폐점":"입점")
                    .build();
        }
    }

    @Builder
    @Getter
    // 가맹점 목록 상세 조회
    public static class StoreDetailListRes{
        private Long idx;
        private String storeName;
        private String ownerName;
        private String ownerEmail;
        private String address;
        private String business;
        private String createdAt;
        private String closedAt;

        public static StoreDto.StoreDetailListRes from(Store entity){

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            return StoreDetailListRes.builder()
                    .idx(entity.getIdx())
                    .storeName(entity.getStoreName())
                    .ownerName(entity.getUser().getUserName())
                    .ownerEmail(entity.getUser().getEmail())
                    .address(entity.getAddress() + " " + entity.getAddressDetail())
                    .business(entity.getBusiness())
                    .createdAt(entity.getCreatedAt().format(formatter))
                    .closedAt(entity.getClosedAt() == null ? "운영 중" : entity.getClosedAt().format(formatter))
                    .build();
        }
    }
}
