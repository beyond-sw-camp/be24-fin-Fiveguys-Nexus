package com.example.nexus.domain.dashboard.model;

import com.example.nexus.domain.delivery.model.Delivery;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class DashboardDto {

    @Getter
    @Builder
    public static class StoreKpiRes {
        private long totalStoreCount;
        private long newStoreCountThisMonth;
        private double deltaPercent;
    }

    @Getter
    @Builder
    public static class RevenueKpiRes {
        private long monthlyRevenue;
        private long todayRevenue;
    }

    @Getter
    @Builder
    public static class OrdersKpiRes {
        private long todayAutoCount;
        private long confirmedCount;
        private long todayManualCount;
    }

    @Getter
    @Builder
    public static class DeliveryKpiRes {
        private long ongoingCount;
        private long delayCount;
        private List<DeliveryItem> deliveryList;
    }

    @Getter
    @Builder
    public static class DeliveryItem {
        private Long deliveryIdx;
        private Long ordersIdx;
        private String storeName;
        private String status;

        public static DeliveryItem from(Delivery entity) {
            return DeliveryItem.builder()
                    .deliveryIdx(entity.getIdx())
                    .ordersIdx(entity.getOrders().getIdx())
                    .storeName(entity.getOrders().getStore().getStoreName())
                    .status(entity.getDeliveryStatus().name())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class DangerStatsRes {
        private List<String> labels;
        private List<Long> confirmed;
        private List<Long> approved;
        private List<Long> rejected;
    }

    @Getter
    @Builder
    public static class WeeklyOrderStatsRes {
        private List<String> labels;
        private List<Long> thisWeek;
        private List<Long> lastWeek;
    }

    @Getter
    @Builder
    public static class DeliveryRatioRes {
        private long ready;
        private long start;
        private long delivering;
        private long delivered;
        private long delay;
    }
}
