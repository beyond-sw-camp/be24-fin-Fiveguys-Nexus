package com.example.nexus.domain.dashboard.model;

import com.example.nexus.domain.delivery.model.Delivery;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class StoreDashboardDto {

    @Getter
    @Builder
    public static class SalesKpiRes {
        private long todaySales;
        private double deltaPercent;
    }

    @Getter
    @Builder
    public static class PendingOrderKpiRes {
        private long pendingCount;
    }

    @Getter
    @Builder
    public static class InventoryRiskKpiRes {
        private long lowCount;
        private long criticalCount;
        private long totalDangerCount;
    }

    @Getter
    @Builder
    public static class SettlementKpiRes {
        private long currentAmount;
        private String currentPeriod;
        private double deltaPercent;
    }

    @Getter
    @Builder
    public static class DailySalesChartRes {
        private List<String> labels;
        private List<Long> thisWeek;
        private List<Long> lastWeek;
    }

    @Getter
    @Builder
    public static class MyDeliveryItem {
        private Long deliveryIdx;
        private Long ordersIdx;
        private String status;
        private String estimatedArrival;

        public static MyDeliveryItem from(Delivery entity) {
            return MyDeliveryItem.builder()
                    .deliveryIdx(entity.getIdx())
                    .ordersIdx(entity.getOrders().getIdx())
                    .status(entity.getDeliveryStatus().name())
                    .estimatedArrival(entity.getEstimatedArrivalAt().toLocalDate().toString())
                    .build();
        }
    }

}
