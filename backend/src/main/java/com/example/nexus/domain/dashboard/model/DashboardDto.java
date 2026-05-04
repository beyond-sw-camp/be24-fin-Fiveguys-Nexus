package com.example.nexus.domain.dashboard.model;

import lombok.Builder;
import lombok.Getter;

public class DashboardDto {

    @Getter
    @Builder
    public static class StoreKpiRes {
        private long totalStoreCount;
        private long newStoreCountThisMonth;
        private double deltaPercent;
    }
}
