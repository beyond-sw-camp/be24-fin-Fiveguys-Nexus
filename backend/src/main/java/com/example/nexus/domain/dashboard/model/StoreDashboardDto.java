package com.example.nexus.domain.dashboard.model;

import lombok.Builder;
import lombok.Getter;

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
}
