package com.example.nexus.domain.dashboard.model;

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
    }
}
