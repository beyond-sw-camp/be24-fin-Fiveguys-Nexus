package com.example.nexus.domain.orders.model;

import com.example.nexus.common.enums.OrdersStatus;
import com.example.nexus.common.enums.OrdersType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class OrdersDto {

    @Getter
    @NoArgsConstructor
    public static class OrdersReq {
        private Long storeIdx;
        private List<OrdersItemDto.OrdersItemReq> ordersItemList;
    }

    @Getter
    @Builder
    public static class OrdersRes {
        private Long idx;
        private Long price;
        private OrdersType ordersType;
        private OrdersStatus ordersStatus;
        private boolean isDanger;
        private LocalDateTime createdAt;
        private String storeName;
        private List<OrdersItemDto.OrdersItemRes> ordersItemList;
        private Long deliveryIdx;

        public static OrdersRes from(Orders entity) {
            return OrdersRes.builder()
                    .idx(entity.getIdx())
                    .price(entity.getPrice())
                    .ordersType(entity.getOrdersType())
                    .ordersStatus(entity.getOrdersStatus())
                    .isDanger(entity.isDanger())
                    .createdAt(entity.getCreatedAt())
                    .storeName(entity.getStore().getStoreName())
                    .ordersItemList(entity.getOrdersItemList().stream()
                            .map(OrdersItemDto.OrdersItemRes::from).toList())
                    .deliveryIdx(entity.getDelivery() != null ? entity.getDelivery().getIdx() : null)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class OrderListRes {
        private Long idx;
        private Long price;
        private OrdersStatus ordersStatus;
        private LocalDateTime createdAt;
        private String storeName;

        public static OrderListRes from(Orders entity) {
            return OrderListRes.builder()
                    .idx(entity.getIdx())
                    .price(entity.getPrice())
                    .ordersStatus(entity.getOrdersStatus())
                    .createdAt(entity.getCreatedAt())
                    .storeName(entity.getStore().getStoreName())
                    .build();
        }
    }
}
