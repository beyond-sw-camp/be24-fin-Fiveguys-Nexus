package com.example.nexus.domain.dashboard;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.dashboard.model.DashboardDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    /**
     * 가맹점 현황 KPI 조회
     *
     * @return ResponseEntity 총 매장 수, 이번 달 신규 매장 수, 전일 대비 증감률
     */
    @GetMapping("/store/kpi")
    public ResponseEntity<BaseResponse> getStoreKpi() {
        DashboardDto.StoreKpiRes result = dashboardService.getStoreKpi();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 총 매출 KPI 조회
     *
     * @return ResponseEntity 이번 달 총 매출, 금일 매출
     */
    @GetMapping("/revenue/kpi")
    public ResponseEntity<BaseResponse> getRevenueKpi() {
        DashboardDto.RevenueKpiRes result = dashboardService.getRevenueKpi();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 발주 현황 KPI 조회
     *
     * @return ResponseEntity 금일 자동 발주 건수, 확정 발주 건수, 금일 수동 발주 건수
     */
    @GetMapping("/orders/kpi")
    public ResponseEntity<BaseResponse> getOrdersKpi() {
        DashboardDto.OrdersKpiRes result = dashboardService.getOrdersKpi();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 배송 현황 KPI 조회
     *
     * @return ResponseEntity 진행중 배송 건수, 지연 건수, 지연 배송 목록
     */
    @GetMapping("/delivery/kpi")
    public ResponseEntity<BaseResponse> getDeliveryKpi() {
        DashboardDto.DeliveryKpiRes result = dashboardService.getDeliveryKpi();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 이상 발주 통계 조회
     *
     * @return ResponseEntity 최근 6개월 월별 이상 발주 상태별 건수
     */
    @GetMapping("/orders/danger/stats")
    public ResponseEntity<BaseResponse> getDangerStats() {
        DashboardDto.DangerStatsRes result = dashboardService.getDangerStats();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 주간 발주 통계 조회
     *
     * @return ResponseEntity 최근 7일 일별 자동/수동 발주 건수
     */
    @GetMapping("/orders/weekly/stats")
    public ResponseEntity<BaseResponse> getWeeklyOrderStats() {
        DashboardDto.WeeklyOrderStatsRes result = dashboardService.getWeeklyOrderStats();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 배송 비율 조회
     *
     * @return ResponseEntity 배송 상태별 건수
     */
    @GetMapping("/delivery/ratio")
    public ResponseEntity<BaseResponse> getDeliveryRatio() {
        DashboardDto.DeliveryRatioRes result = dashboardService.getDeliveryRatio();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 위험 재고 목록 조회
     *
     * @return ResponseEntity LOW/CRITICAL 상태 본사 재고 목록
     */
    @GetMapping("/inventory/danger")
    public ResponseEntity<BaseResponse> getDangerInventoryList() {
        List<DashboardDto.DangerInventoryItem> result = dashboardService.getDangerInventoryList();
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
