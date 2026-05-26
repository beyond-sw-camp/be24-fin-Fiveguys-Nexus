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

@RequiredArgsConstructor
@RestController
public class SettlementController {

    private final HeadIncomeService headIncomeService;
    private final StoreService storeService;
    private final SettlementService settlementService;
    private final OrdersService ordersService;

    @GetMapping("/unpaid/list")
    public ResponseEntity<List<SettlementDto.FinalRetryFailRes>> getUnpaidList(
            @AuthenticationPrincipal AuthUserDetails authUserDetails) {

        // 로그인 한 사용자의 지점 번호 받아오기
        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());

        // 재시도까지 최종 실패한 목록 가져오기
        List<SettlementDto.FinalRetryFailRes> finalFailures = settlementService.getFinalRetryFailures(storeIdx);

        return ResponseEntity.ok(finalFailures);
    }


    @PostMapping("/pay")
    public ResponseEntity pay (@AuthenticationPrincipal AuthUserDetails authUserDetails, @RequestBody SettlementDto.PayReq reqDto) {

        // 로그인 한 사용자의 storeIdx 가져오기
        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());

        // verify 전 단계 완료
        Long settlementIdx = settlementService.pay(storeIdx, reqDto);
        
        
        // settlementIdx = null 처리
        
        
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

    @PostMapping("/verify")
    public ResponseEntity verify (@AuthenticationPrincipal AuthUserDetails authUserDetails, @RequestBody SettlementDto.VerifyReq dto) {

        LocalDateTime now = LocalDateTime.now();
        YearMonth currentYearMonth = YearMonth.from(now);
        YearMonth lastMonth = currentYearMonth.minusMonths(1);
        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());
        // service 에서 현재 날짜와 로그인 한 사용자의 store_idx 로 찾아서 settlementIdx 를 return 하는 api 찾기
        System.out.println(storeIdx);
        Long settlementIdx = settlementService.findSettlementIdx(storeIdx, lastMonth);

        System.out.println(dto.getPaymentId());
        settlementService.verify(authUserDetails, dto, settlementIdx);
        return ResponseEntity.ok("결제 성공");
    }


}
