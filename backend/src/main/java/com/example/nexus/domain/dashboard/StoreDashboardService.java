package com.example.nexus.domain.dashboard;

import com.example.nexus.common.exception.BaseException;
import com.example.nexus.common.model.BaseResponseStatus;
import com.example.nexus.domain.dashboard.model.StoreDashboardDto;
import com.example.nexus.domain.pos.PosPayRepository;
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
        long todaySales = posPayRepository.sumPayAmountByStoreAndPaidAtBetween(store.getIdx(), todayStart, todayEnd);
        long yesterdaySales = posPayRepository.sumPayAmountByStoreAndPaidAtBetween(store.getIdx(), yesterdayStart, todayStart);

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
}
