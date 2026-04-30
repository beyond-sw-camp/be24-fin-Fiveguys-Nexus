package com.example.nexus.domain.store.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        private Integer postcode;
        private String address;
        private String addressDetail;
        private String business;
        private String createdAt;
        private String closedAt;
        private String filePath;

        public static StoreDto.StoreDetailListRes from(Store entity){

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            return StoreDetailListRes.builder()
                    .idx(entity.getIdx())
                    .storeName(entity.getStoreName())
                    .ownerName(entity.getUser().getUserName())
                    .ownerEmail(entity.getUser().getEmail())
                    .postcode(entity.getPostcode())
                    .address(entity.getAddress())
                    .addressDetail(entity.getAddressDetail())
                    .business(entity.getBusiness())
                    .createdAt(entity.getCreatedAt().format(formatter))
                    .closedAt(
                            entity.getClosedAt() == null && !entity.isDeleted()
                                    ? "운영 중"
                                    : entity.getClosedAt().format(formatter)

                    )
                    .filePath(entity.getFilePath())
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

    // 신규 가맹점 등록 req
    @Getter
    public static class StoreRegReq{
        private String storeName;
        private String ownerEmail;
        private Integer postcode;
        private String address;
        private String addressDetail;
        private String business;
        private String filePath;

        public Store toEntity(){
            return Store.builder()
                    .storeName(this.storeName)
                    .postcode(this.postcode)
                    .address(this.address)
                    .addressDetail(this.addressDetail)
                    .business(this.business)
                    .filePath(this.filePath)
                    .build();
        }
    }
}
