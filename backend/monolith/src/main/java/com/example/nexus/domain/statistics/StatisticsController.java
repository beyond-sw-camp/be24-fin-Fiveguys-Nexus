package com.example.nexus.domain.statistics;

import com.example.nexus.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    /**
     * 오늘 실시간 총 매출 조회
     *
     * @return ResponseEntity 오늘 전체 매출 금액
     */
    @GetMapping("/sales/today")
    public ResponseEntity<BaseResponse> getTodaySales() {
        return ResponseEntity.ok(BaseResponse.success(statisticsService.getTodaySales()));
    }

    /**
     * 매장 매출 랭킹 TOP 5 조회
     *
     * @return ResponseEntity 매출 상위 매장 5개 (매장명, 매출액)
     */
    @GetMapping("/store/top")
    public ResponseEntity<BaseResponse> getTopStores() {
        return ResponseEntity.ok(BaseResponse.success(statisticsService.getTopStores()));
    }

    /**
     * 인기 메뉴 TOP 5 조회
     *
     * @return ResponseEntity 판매 수량 상위 메뉴 5개 (메뉴명, 판매 수량)
     */
    @GetMapping("/menu/top")
    public ResponseEntity<BaseResponse> getTopMenus() {
        return ResponseEntity.ok(BaseResponse.success(statisticsService.getTopMenus()));
    }

    /**
     * 시간대별 매출 추이 조회 (0~23시)
     *
     * @return ResponseEntity 시간대별 매출 금액 리스트
     */
    @GetMapping("/sales/hourly")
    public ResponseEntity<BaseResponse> getHourlySales() {
        return ResponseEntity.ok(BaseResponse.success(statisticsService.getHourlySales()));
    }

    /**
     * 카테고리별 매출 비율 조회
     *
     * @return ResponseEntity 메뉴 카테고리별 매출 금액 리스트
     */
    @GetMapping("/category/ratio")
    public ResponseEntity<BaseResponse> getCategoryRatio() {
        return ResponseEntity.ok(BaseResponse.success(statisticsService.getCategoryRatio()));
    }

    /**
     * 결제 수단별 매출 비율 조회
     *
     * @return ResponseEntity 결제 수단별 매출 금액 리스트
     */
    @GetMapping("/payment/ratio")
    public ResponseEntity<BaseResponse> getPaymentMethodRatio() {
        return ResponseEntity.ok(BaseResponse.success(statisticsService.getPaymentMethodRatio()));
    }
}
