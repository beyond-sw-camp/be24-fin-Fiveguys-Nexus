package com.example.nexus.domain.dashboard;

import com.example.nexus.common.enums.InventoryStatus;
import com.example.nexus.common.enums.OrdersStatus;
import com.example.nexus.common.enums.OrdersType;
import com.example.nexus.common.exception.BaseException;
import com.example.nexus.common.model.BaseResponseStatus;
import com.example.nexus.domain.dashboard.model.StoreDashboardDto;
import com.example.nexus.domain.orders.OrdersRepository;
import com.example.nexus.domain.pos.PosPayRepository;
import com.example.nexus.domain.store.StoreInventoryRepository;
import com.example.nexus.domain.store.StoreRepository;
import com.example.nexus.domain.store.model.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StoreDashboardService {
    private final PosPayRepository posPayRepository;
    private final OrdersRepository ordersRepository;
    private final StoreInventoryRepository storeInventoryRepository;
    private final StoreRepository storeRepository;

    /**
     * 금일 매출 KPI 조회
     * - 로그인된 점주의 매장에서 당일 발생한 POS 매출 총액을 집계
     * - 전일 매출과 비교하여 증감률(%) 계산
     */
    public StoreDashboardDto.SalesKpiRes getSalesKpi(Long userIdx) {
        // 점주의 매장 조회
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        // 금일/전일 기간 설정
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
        LocalDateTime yesterdayStart = today.minusDays(1).atStartOfDay();

        // POS 매출 합산
        long todaySales = posPayRepository.sumPayAmountByStoreAndPeriod(store.getIdx(), todayStart, todayEnd);
        long yesterdaySales = posPayRepository.sumPayAmountByStoreAndPeriod(store.getIdx(), yesterdayStart, todayStart);

        // 전일 대비 증감률 계산 (전일 매출이 0이면 0.0%)
        double deltaPercent = 0.0;
        if (yesterdaySales > 0) {
            deltaPercent = Math.round((todaySales - yesterdaySales) * 1000.0 / yesterdaySales) / 10.0;
        }

        return StoreDashboardDto.SalesKpiRes.builder()
                .todaySales(todaySales)
                .deltaPercent(deltaPercent)
                .build();
    }

    /**
     * 제안 발주서 KPI 조회
     * - 점주 매장에 대해 본사가 자동 생성한 발주서 중 승인 대기(WAITING) 상태 건수 반환
     */
    public StoreDashboardDto.PendingOrderKpiRes getPendingOrderKpi(Long userIdx) {
        // 점주의 매장 조회
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        // 승인 대기 상태의 자동 발주 건수 조회
        long pendingCount = ordersRepository.countByStore_IdxAndOrdersStatusAndOrdersType(
                store.getIdx(), OrdersStatus.WAITING, OrdersType.AUTO);

        return StoreDashboardDto.PendingOrderKpiRes.builder()
                .pendingCount(pendingCount)
                .build();
    }

    /**
     * 재고 위험 품목 KPI 조회
     * - 점주 매장의 재고 중 LOW(주의)/CRITICAL(위험) 상태인 품목 수 반환
     */
    public StoreDashboardDto.InventoryRiskKpiRes getInventoryRiskKpi(Long userIdx) {
        // 점주의 매장 조회
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        // 상태별 위험 재고 건수 조회
        long lowCount = storeInventoryRepository.countByStore_IdxAndStatus(store.getIdx(), InventoryStatus.LOW);
        long criticalCount = storeInventoryRepository.countByStore_IdxAndStatus(store.getIdx(), InventoryStatus.CRITICAL);

        return StoreDashboardDto.InventoryRiskKpiRes.builder()
                .lowCount(lowCount)
                .criticalCount(criticalCount)
                .totalDangerCount(lowCount + criticalCount)
                .build();
    }
}
