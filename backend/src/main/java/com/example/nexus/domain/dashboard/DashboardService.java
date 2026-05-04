package com.example.nexus.domain.dashboard;

import com.example.nexus.domain.dashboard.model.DashboardDto;
import com.example.nexus.domain.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final StoreRepository storeRepository;

    public DashboardDto.StoreKpiRes getStoreKpi() {
        long totalCount = storeRepository.countByIsDeletedFalse();

        // 이번 달 1일 00:00 기준 신규 매장 수
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long newThisMonth = storeRepository.countByIsDeletedFalseAndCreatedAtAfter(monthStart);

        // 전일 대비 증감률: 어제 매장 수 대비 오늘 매장 수
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime yesterdayStart = todayStart.minusDays(1);
        long newToday = storeRepository.countByIsDeletedFalseAndCreatedAtAfter(todayStart);
        long newYesterday = storeRepository.countByIsDeletedFalseAndCreatedAtAfter(yesterdayStart) - newToday;

        double delta = newYesterday > 0 ? (double) (newToday - newYesterday) * 100 / newYesterday : 0;

        return DashboardDto.StoreKpiRes.builder()
                .totalStoreCount(totalCount)
                .newStoreCountThisMonth(newThisMonth)
                .deltaPercent(Math.round(delta * 10) / 10.0)
                .build();
    }
}
