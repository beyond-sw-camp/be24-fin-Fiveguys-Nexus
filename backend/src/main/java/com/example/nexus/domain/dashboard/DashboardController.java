package com.example.nexus.domain.dashboard;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.dashboard.model.DashboardDto;
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
}
