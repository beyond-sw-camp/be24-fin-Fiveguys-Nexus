package com.example.nexus.domain.dashboard;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.dashboard.model.DashboardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    // === 실시간 대시보드 API (부하 테스트용 DB 직접 쿼리 방식) ===

    /**
     * 오늘 전체 매장 실시간 매출 합계 조회
     *
     * @return ResponseEntity 오늘 전체 매출 금액
     */
    @GetMapping("/realtime/sales/today")
    public ResponseEntity<BaseResponse> getTodaySales() {
        return ResponseEntity.ok(BaseResponse.success(dashboardService.getTodaySales()));
    }

    /**
     * 매장별 매출 랭킹 TOP 3 조회
     *
     * @return ResponseEntity 매출 상위 매장 3개 (매장명, 매출액)
     */
    @GetMapping("/realtime/store/top")
    public ResponseEntity<BaseResponse> getTopStores() {
        return ResponseEntity.ok(BaseResponse.success(dashboardService.getTopStores()));
    }

    /**
     * 메뉴별 판매 수량 랭킹 TOP 3 조회
     *
     * @return ResponseEntity 판매 수량 상위 메뉴 3개 (메뉴명, 판매 수량)
     */
    @GetMapping("/realtime/menu/top")
    public ResponseEntity<BaseResponse> getTopMenus() {
        return ResponseEntity.ok(BaseResponse.success(dashboardService.getTopMenus()));
    }

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
     * 재고 위험 KPI 조회
     *
     * @return ResponseEntity LOW/CRITICAL 상태 품목 수
     */
    @GetMapping("/inventory/kpi")
    public ResponseEntity<BaseResponse> getInventoryKpi() {
        DashboardDto.InventoryKpiRes result = dashboardService.getInventoryKpi();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 배송 현황 KPI 조회
     *
     * @return ResponseEntity 진행중 배송 건수, 지연 배송 목록
     */
    @GetMapping("/delivery/kpi")
    public ResponseEntity<BaseResponse> getDeliveryKpi() {
        DashboardDto.DeliveryKpiRes result = dashboardService.getDeliveryKpi();
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
     * 위험 재고 목록 조회
     *
     * @param page 페이지 번호 (0부터 시작, 기본값 0)
     * @param size 페이지 크기 (기본값 4)
     * @return ResponseEntity LOW/CRITICAL 상태 본사 재고 목록 (Slice 페이징)
     */
    @GetMapping("/inventory/danger")
    public ResponseEntity<BaseResponse> getDangerInventoryList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
        DashboardDto.DangerInventoryRes result = dashboardService.getDangerInventoryList(page, size);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 이상 발주 통계 조회
     *
     * @return ResponseEntity 최근 6개월 월별 이상 발주 상태별 건수
     */
    @GetMapping("/orders/danger/stats")
    public ResponseEntity<BaseResponse> getOrdersDangerStats() {
        DashboardDto.DangerStatsRes result = dashboardService.getOrdersDangerStats();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 지연 배송 목록 조회
     *
     * @param page 페이지 번호 (0부터 시작, 기본값 0)
     * @param size 페이지 크기 (기본값 4)
     * @return ResponseEntity 지연 배송 목록 (Slice 페이징)
     */
    @GetMapping("/delivery/delay")
    public ResponseEntity<BaseResponse> getDelayDeliveryList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
        DashboardDto.DelayDeliveryRes result = dashboardService.getDelayDeliveryList(page, size);
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
}
