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
    @GetMapping("/store-kpi")
    public ResponseEntity<BaseResponse> getStoreKpi() {
        DashboardDto.StoreKpiRes result = dashboardService.getStoreKpi();
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
