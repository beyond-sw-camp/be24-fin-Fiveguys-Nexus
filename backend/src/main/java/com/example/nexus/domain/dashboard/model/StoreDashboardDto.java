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
}
