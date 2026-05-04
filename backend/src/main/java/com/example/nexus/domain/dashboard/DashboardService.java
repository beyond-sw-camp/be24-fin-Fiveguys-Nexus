package com.example.nexus.domain.dashboard;

import com.example.nexus.common.enums.DeliveryStatus;
import com.example.nexus.common.enums.OrdersStatus;
import com.example.nexus.common.enums.OrdersType;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import com.example.nexus.domain.dashboard.model.DashboardDto;
import com.example.nexus.domain.delivery.DeliveryRepository;
import com.example.nexus.domain.head.HeadIncomeRepository;
import com.example.nexus.domain.orders.OrdersRepository;
import com.example.nexus.domain.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final StoreRepository storeRepository;
    private final HeadIncomeRepository headIncomeRepository;
    private final OrdersRepository ordersRepository;
    private final DeliveryRepository deliveryRepository;

    public DashboardDto.StoreKpiRes getStoreKpi() {
        long totalCount = storeRepository.countByIsDeletedFalse();

        // 이번 달 1일 00:00 기준 신규 매장 수
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long newThisMonth = storeRepository.countByIsDeletedFalseAndCreatedAtAfter(monthStart);

        // 전일 대비 증감률: 어제까지 총 매장 수 vs 오늘까지 총 매장 수
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long yesterdayTotal = storeRepository.countByIsDeletedFalseAndCreatedAtBefore(todayStart);
        double delta = yesterdayTotal > 0 ? (double) (totalCount - yesterdayTotal) * 100 / yesterdayTotal : 0;

        return DashboardDto.StoreKpiRes.builder()
                .totalStoreCount(totalCount)
                .newStoreCountThisMonth(newThisMonth)
                .deltaPercent(Math.round(delta * 10) / 10.0)
                .build();
    }

    public DashboardDto.RevenueKpiRes getRevenueKpi() {
        // 이번 달 1일 00:00 기준 월간 매출
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long monthlyRevenue = headIncomeRepository.sumPriceByOrdersCreatedAtAfter(monthStart);

        // 금일 00:00 기준 오늘 매출
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayRevenue = headIncomeRepository.sumPriceByOrdersCreatedAtAfter(todayStart);

        return DashboardDto.RevenueKpiRes.builder()
                .monthlyRevenue(monthlyRevenue)
                .todayRevenue(todayRevenue)
                .build();
    }

    public DashboardDto.OrdersKpiRes getOrdersKpi() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        // 금일 자동 발주 건수
        long todayAutoCount = ordersRepository.countByOrdersTypeAndCreatedAtAfter(OrdersType.AUTO, todayStart);

        // 현재 확정 상태 발주 건수
        long confirmedCount = ordersRepository.countByOrdersStatus(OrdersStatus.CONFIRMED);

        // 금일 수동 발주 건수
        long todayManualCount = ordersRepository.countByOrdersTypeAndCreatedAtAfter(OrdersType.MANUAL, todayStart);

        return DashboardDto.OrdersKpiRes.builder()
                .todayAutoCount(todayAutoCount)
                .confirmedCount(confirmedCount)
                .todayManualCount(todayManualCount)
                .build();
    }

    public DashboardDto.DeliveryKpiRes getDeliveryKpi() {
        List<DeliveryStatus> ongoingStatuses = List.of(
                DeliveryStatus.READY, DeliveryStatus.START, DeliveryStatus.DELIVERYING);

        // 진행중 배송 건수
        long ongoingCount = deliveryRepository.countByDeliveryStatusIn(ongoingStatuses);

        // 배송 지연 건수
        long delayCount = deliveryRepository.countByDeliveryStatus(DeliveryStatus.DELAY);

        // 배송 지연 목록
        List<DashboardDto.DeliveryItem> deliveryList = deliveryRepository
                .findByDeliveryStatus(DeliveryStatus.DELAY).stream()
                .map(DashboardDto.DeliveryItem::from)
                .toList();

        return DashboardDto.DeliveryKpiRes.builder()
                .ongoingCount(ongoingCount)
                .delayCount(delayCount)
                .deliveryList(deliveryList)
                .build();
    }

    public DashboardDto.DangerStatsRes getDangerStats() {
        // 조회 시작 시점: 현재 월 포함 6개월 전 1일 00:00
        // 예: 현재 2026-05 → 2025-12-01 00:00부터 조회
        LocalDateTime since = LocalDate.now().minusMonths(5).withDayOfMonth(1).atStartOfDay();

        // 6개월치 라벨(yyyy-MM)을 순서대로 생성하고, 각 월별 카운트 배열 초기화
        // LinkedHashMap으로 삽입 순서 보장 → 프론트 차트 x축 순서 유지
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, long[]> monthMap = new LinkedHashMap<>();
        for (int i = 0; i < 6; i++) {
            String label = YearMonth.now().minusMonths(5 - i).format(formatter);
            monthMap.put(label, new long[3]); // [0: 승인대기(CONFIRMED), 1: 승인(APPROVE), 2: 반려(REJECT)]
        }

        // DB에서 월별 + 상태별 이상 발주 건수 조회 후 monthMap에 매핑
        List<Object[]> rows = ordersRepository.findDangerStatsByMonth(since);
        for (Object[] row : rows) {
            String month = (String) row[0];
            long count = (Long) row[2];

            long[] counts = monthMap.get(month);
            if (counts == null) {
                continue;
            }

            OrdersStatus status = (OrdersStatus) row[1];
            switch (status) {
                case CONFIRMED :
                    counts[0] = count;
                    break; // 승인 대기
                case APPROVE :
                    counts[1] = count;
                    break; // 승인
                case REJECT :
                    counts[2] = count;
                    break; // 반려
                default: break;
            }
        }

        // monthMap을 프론트 차트에 맞는 배열 형태로 변환
        List<String> labels = new ArrayList<>(monthMap.keySet());
        List<Long> confirmed = new ArrayList<>();
        List<Long> approved = new ArrayList<>();
        List<Long> rejected = new ArrayList<>();

        for (long[] counts : monthMap.values()) {
            confirmed.add(counts[0]);
            approved.add(counts[1]);
            rejected.add(counts[2]);
        }

        return DashboardDto.DangerStatsRes.builder()
                .labels(labels)
                .confirmed(confirmed)
                .approved(approved)
                .rejected(rejected)
                .build();
    }
}
