package com.example.nexus.domain.orders.model;

import lombok.Builder;
import lombok.Getter;

public class OrdersItemDto {

    @Getter
    @Builder
    public static class OrdersItemReq {
        private Integer count;
        private Long productIdx;
    }

    @Getter
    @Builder
    public static class OrdersItemRes {
        private Long idx;
        private Integer count;
        private Long orderIdx;
        private Long productIdx;

        public static OrdersItemRes from(OrdersItem entity) {
            return OrdersItemRes.builder()
                    .idx(entity.getIdx())
                    .count(entity.getCount())
                    .orderIdx(entity.getOrders().getIdx())
                    .productIdx(entity.getProduct().getIdx())
                    .build();
        }
    }

}
