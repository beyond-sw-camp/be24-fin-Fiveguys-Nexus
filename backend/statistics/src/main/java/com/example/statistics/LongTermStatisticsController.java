package com.example.statistics;

import com.example.statistics.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 장기 통계 (월/연도/매장/카테고리별) REST API.
 * daily_* 테이블의 영구 보존 데이터를 기반으로 조회.
 *
 * URL prefix: /statistics/long-term/*
 */
@RestController
@RequestMapping("/statistics/long-term")
@RequiredArgsConstructor
public class LongTermStatisticsController {

    private final LongTermStatisticsService longTermStatisticsService;

    /**
     * 연도별 총 매출 (전체 기간).
     *
     * GET /statistics/long-term/yearly
     */
    @GetMapping("/yearly")
    public ResponseEntity<BaseResponse> getYearly() {
        return ResponseEntity.ok(BaseResponse.success(longTermStatisticsService.getYearlySales()));
    }

    /**
     * 특정 연도의 월별 매출 (1~12월).
     *
     * GET /statistics/long-term/monthly?year=2026
     *
     * @param year 조회할 연도
     */
    @GetMapping("/monthly")
    public ResponseEntity<BaseResponse> getMonthly(@RequestParam int year) {
        return ResponseEntity.ok(BaseResponse.success(longTermStatisticsService.getMonthlySales(year)));
    }

    /**
     * 특정 기간 매장별 매출 (매출 큰 순).
     *
     * GET /statistics/long-term/stores?year=2026&month=5   (월 단위)
     * GET /statistics/long-term/stores?year=2026           (연 단위)
     *
     * @param year  조회할 연도
     * @param month 조회할 월 (선택, 없으면 그 해 전체)
     */
    @GetMapping("/stores")
    public ResponseEntity<BaseResponse> getStores(
            @RequestParam int year,
            @RequestParam(required = false) Integer month
    ) {
        return ResponseEntity.ok(BaseResponse.success(
                longTermStatisticsService.getStoreSales(year, month)
        ));
    }

    /**
     * 특정 기간 카테고리별 매출 (매출 큰 순).
     *
     * GET /statistics/long-term/categories?year=2026&month=5
     * GET /statistics/long-term/categories?year=2026
     *
     * @param year  조회할 연도
     * @param month 조회할 월 (선택, 없으면 그 해 전체)
     */
    @GetMapping("/categories")
    public ResponseEntity<BaseResponse> getCategories(
            @RequestParam int year,
            @RequestParam(required = false) Integer month
    ) {
        return ResponseEntity.ok(BaseResponse.success(
                longTermStatisticsService.getCategorySales(year, month)
        ));
    }

    /**
     * 특정 기간 메뉴별 판매량 (판매량 많은 순).
     *
     * GET /statistics/long-term/menus?year=2026&month=5
     * GET /statistics/long-term/menus?year=2026
     *
     * @param year  조회할 연도
     * @param month 조회할 월 (선택, 없으면 그 해 전체)
     */
    @GetMapping("/menus")
    public ResponseEntity<BaseResponse> getMenus(
            @RequestParam int year,
            @RequestParam(required = false) Integer month
    ) {
        return ResponseEntity.ok(BaseResponse.success(
                longTermStatisticsService.getMenuSales(year, month)
        ));
    }
}
