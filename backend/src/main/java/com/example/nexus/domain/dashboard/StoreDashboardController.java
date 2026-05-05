package com.example.nexus.domain.dashboard;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.dashboard.model.StoreDashboardDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/store/dashboard")
@RequiredArgsConstructor
public class StoreDashboardController {
    private final StoreDashboardService storeDashboardService;

    /**
     * 금일 매출 KPI 조회
     *
     * @param authUserDetails 인증된 사용자 정보
     * @return ResponseEntity 금일 매출 총액, 전일 대비 증감률
     */
    @GetMapping("/sales/kpi")
    public ResponseEntity<BaseResponse> getSalesKpi(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        StoreDashboardDto.SalesKpiRes result = storeDashboardService.getSalesKpi(authUserDetails.getIdx());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 제안 발주서 KPI 조회
     *
     * @param authUserDetails 인증된 사용자 정보
     * @return ResponseEntity 승인 대기 상태의 제안 발주서 건수
     */
    @GetMapping("/orders/pending/kpi")
    public ResponseEntity<BaseResponse> getPendingOrderKpi(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        StoreDashboardDto.PendingOrderKpiRes result = storeDashboardService.getPendingOrderKpi(authUserDetails.getIdx());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 재고 위험 품목 KPI 조회
     *
     * @param authUserDetails 인증된 사용자 정보
     * @return ResponseEntity LOW(주의)/CRITICAL(위험) 상태 품목 수
     */
    @GetMapping("/inventory/risk/kpi")
    public ResponseEntity<BaseResponse> getInventoryRiskKpi(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        StoreDashboardDto.InventoryRiskKpiRes result = storeDashboardService.getInventoryRiskKpi(authUserDetails.getIdx());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 정산 현황 KPI 조회
     *
     * @param authUserDetails 인증된 사용자 정보
     * @return ResponseEntity 현재 반월 구간 정산 금액, 구간명, 직전 구간 대비 증감률
     */
    @GetMapping("/settlement/kpi")
    public ResponseEntity<BaseResponse> getSettlementKpi(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        StoreDashboardDto.SettlementKpiRes result = storeDashboardService.getSettlementKpi(authUserDetails.getIdx());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 일별 매출 추이 차트 조회
     *
     * @param authUserDetails 인증된 사용자 정보
     * @return ResponseEntity 이번 주/지난 주 요일별 매출 데이터
     */
    @GetMapping("/sales/daily")
    public ResponseEntity<BaseResponse> getDailySalesChart(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        StoreDashboardDto.DailySalesChartRes result = storeDashboardService.getDailySalesChart(authUserDetails.getIdx());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 나의 배송 현황 목록 조회
     *
     * @param authUserDetails 인증된 사용자 정보
     * @return ResponseEntity 배송 진행중인 건의 상태 목록
     */
    @GetMapping("/delivery/list")
    public ResponseEntity<BaseResponse> getMyDeliveryList(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        List<StoreDashboardDto.MyDeliveryItem> result = storeDashboardService.getMyDeliveryList(authUserDetails.getIdx());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 제안 발주서 목록 조회
     *
     * @param authUserDetails 인증된 사용자 정보
     * @return ResponseEntity 승인 대기 상태의 자동 발주서 목록
     */
    @GetMapping("/orders/pending/list")
    public ResponseEntity<BaseResponse> getPendingOrderList(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        List<StoreDashboardDto.PendingOrderItem> result = storeDashboardService.getPendingOrderList(authUserDetails.getIdx());
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
