package com.example.nexus.domain.orders.model;

import com.example.nexus.common.enums.OrdersStatus;
import com.example.nexus.common.enums.OrdersType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class OrdersDto {

    @Getter
    @Builder
    public static class OrdersRes {
        private Long idx;
        private Long price;
        private OrdersType ordersType;
        private OrdersStatus ordersStatus;
        private boolean isDanger;
        private LocalDateTime createdAt;
        private Long storeIdx;
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
                    .storeIdx(entity.getStore().getIdx())
                    .ordersItemList(entity.getOrdersItemList().stream()
                            .map(OrdersItemDto.OrdersItemRes::from).toList())
                    .deliveryIdx(entity.getDelivery() != null ? entity.getDelivery().getIdx() : null)
                    .build();
        }
    }
}
