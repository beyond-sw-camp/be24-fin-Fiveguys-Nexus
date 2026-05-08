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
    public ResponseEntity getUnpaidList(
            @AuthenticationPrincipal AuthUserDetails authUserDetails) {

        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());

        YearMonth lastMonth = YearMonth.from(LocalDateTime.now()).minusMonths(1);

        // storeIdx로 모든 주문 찾기
        List<Orders> ordersList = ordersService.findByStoreIdx(storeIdx);

        // 지불하지 않은 HeadIncome 가져오기
        List<HeadIncomeDto.FindHeadIncomeRes> unpaidList = new ArrayList<>();
        Long settlementIdx = 0L;
        for (Orders order : ordersList) {
            YearMonth orderYearMonth = YearMonth.from(order.getCreatedAt());

            if (lastMonth.equals(orderYearMonth)) {
                HeadIncomeDto.FindHeadIncomeRes resDto = headIncomeService.findByOrdersIdx(order.getIdx());
                if (resDto != null && !resDto.isPaid()) {
                    unpaidList.add(resDto);
                    settlementIdx = resDto.getSettlementIdx();
                }
            }
        }

        SettlementDto.unPaid dto = SettlementDto.unPaid.builder()
                .settlementIdx(settlementIdx)
                .unpaidList(unpaidList)
                .build();


        return ResponseEntity.ok(dto);
    }

    @PostMapping("/pay")
    public ResponseEntity pay (@AuthenticationPrincipal AuthUserDetails authUserDetails, @RequestBody SettlementDto.PayReq reqDto) {

        // 로그인 한 사용자의 storeIdx 가져오기
        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());

        // verify 전 단계 완료
        Long settlementIdx = settlementService.pay(storeIdx, reqDto);

        List<HeadIncomeDto.FindHeadIncomeRes> headIncomeResList = new ArrayList<>();

        long headIncome = 0L;


        for (Long idx : reqDto.getHeadIncomeidxList()) {
            HeadIncome headIncome = headIncomeService.findById(idx);
            HeadIncomeDto.FindHeadIncomeRes headIncomeResDto = HeadIncomeDto.FindHeadIncomeRes.builder()
                    .idx(headIncome.getIdx())
                    .price(headIncome.getPrice())
                    .paid(headIncome.getStatus())
                    .settlementIdx(headIncome.getSettlementIdx())
                    .storeIdx(headIncome.getStore().getIdx())
                    .ordersIdx(headIncome.getOrders().getIdx())
                    .build();
            headIncomeResList.add(headIncomeResDto);
        }
        System.out.println("범인 찾기" + headIncome.getSettlementIdx);
        return ResponseEntity.ok(headIncomeResList);
    }

    @PostMapping("/verify")
    public ResponseEntity verify (@AuthenticationPrincipal AuthUserDetails authUserDetails, @RequestBody SettlementDto.VerifyReq dto) {

        LocalDateTime now = LocalDateTime.now();
        YearMonth currentYearMonth = YearMonth.from(now);
        YearMonth lastMonth = currentYearMonth.minusMonths(1);
        System.out.println(dto.getSettlementIdx());
        System.out.println(dto.getPaymentId());
        // dto 에서 1이 줄어들어서 들어옴
        settlementService.verify(authUserDetails, dto, lastMonth);
        return ResponseEntity.ok("결제 성공");
    }


}
