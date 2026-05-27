package com.example.nexus.domain.settlement;

import com.example.nexus.domain.head.HeadIncomeService;
import com.example.nexus.domain.head.model.HeadIncome;
import com.example.nexus.domain.head.model.HeadIncomeDto;
import com.example.nexus.domain.orders.OrdersService;
import com.example.nexus.domain.orders.model.Orders;
import com.example.nexus.domain.settlement.model.Settlement;
import com.example.nexus.domain.settlement.model.SettlementDto;
import com.example.nexus.domain.store.StoreService;
import com.example.nexus.domain.user.model.AuthUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "정산 관리", description = "가맹점 미납 내역 조회 및 결제/검증 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/settlement")
public class SettlementController {

    private final HeadIncomeService headIncomeService;
    private final StoreService storeService;
    private final SettlementService settlementService;
    private final OrdersService ordersService;

    @Operation(summary = "미납 내역 조회", description = "재시도(2차) 결제까지 최종 실패한 미납 내역 목록을 조회합니다.")
    @GetMapping("/unpaid/list")
    public ResponseEntity<List<SettlementDto.FinalRetryFailRes>> getUnpaidList(
            @AuthenticationPrincipal AuthUserDetails authUserDetails) {

        // 로그인 한 사용자의 지점 번호 받아오기
        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());

        // 재시도까지 최종 실패한 목록 가져오기
        List<SettlementDto.FinalRetryFailRes> finalFailures = settlementService.getFinalRetryFailures(storeIdx);

        return ResponseEntity.ok(finalFailures);
    }

    @Operation(summary = "결제 요청", description = "미납된 정산 내역에 대해 결제를 요청합니다.")
    @PostMapping("/pay")
    public ResponseEntity<List<HeadIncomeDto.FindHeadIncomeRes>> pay(
            @AuthenticationPrincipal AuthUserDetails authUserDetails,
            @RequestBody SettlementDto.PayReq reqDto) {

        // 로그인 한 사용자의 storeIdx 가져오기
        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());

        // verify 전 단계 완료
        Long settlementIdx = settlementService.pay(storeIdx, reqDto);
        
        List<HeadIncomeDto.FindHeadIncomeRes> headIncomeResList = new ArrayList<>();

        for (Long idx : reqDto.getHeadIncomeidxList()) {
            HeadIncome headIncome = headIncomeService.findById(idx);
            HeadIncomeDto.FindHeadIncomeRes headIncomeResDto = HeadIncomeDto.FindHeadIncomeRes.builder()
                    .idx(headIncome.getIdx())
                    .price(headIncome.getPrice())
                    .paid(headIncome.getStatus())
                    .storeIdx(headIncome.getStore().getIdx())
                    .ordersIdx(headIncome.getOrders().getIdx())
                    .build();
            headIncomeResList.add(headIncomeResDto);
        }

        return ResponseEntity.ok(headIncomeResList);
    }

    @Operation(summary = "결제 검증", description = "포트원 등을 통한 결제 완료 후 서버 측에서 결제 정보의 유효성을 검증합니다.")
    @PostMapping("/verify")
    public ResponseEntity<String> verify(
            @AuthenticationPrincipal AuthUserDetails authUserDetails,
            @RequestBody SettlementDto.VerifyReq dto) {

        LocalDateTime now = LocalDateTime.now();
        YearMonth currentYearMonth = YearMonth.from(now);
        YearMonth lastMonth = currentYearMonth.minusMonths(1);
        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());
        
        Long settlementIdx = settlementService.findSettlementIdx(storeIdx, lastMonth);

        settlementService.verify(authUserDetails, dto, settlementIdx);
        return ResponseEntity.ok("결제 성공");
    }
}
