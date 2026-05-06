package com.example.nexus.domain.settlement;

import com.example.nexus.domain.head.HeadIncomeService;
import com.example.nexus.domain.head.model.HeadIncome;
import com.example.nexus.domain.head.model.HeadIncomeDto;
import com.example.nexus.domain.orders.OrdersService;
import com.example.nexus.domain.orders.model.Orders;
import com.example.nexus.domain.settlement.model.SettlementDto;
import com.example.nexus.domain.store.StoreService;
import com.example.nexus.domain.user.model.AuthUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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


    @PostMapping("/pay")
    public ResponseEntity pay (@AuthenticationPrincipal AuthUserDetails authUserDetails) {

        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());

        LocalDateTime now = LocalDateTime.now();
        YearMonth currentYearMonth = YearMonth.from(now);

        List<Orders> ordersList = ordersService.findByStoreIdx(storeIdx);

        List<Orders> thisMonthOrderList = new ArrayList<>();

        for (Orders orders : ordersList) {

            YearMonth orderYearMonth = YearMonth.from(orders.getCreatedAt());

            if (currentYearMonth.equals(orderYearMonth)) {
                // 이번 달에 결제/생성된 주문 처리 로직 작성

                thisMonthOrderList.add(orders);
            }
        }

        int totalPrice = 0;
        List<Long> notPaidHeadIncomeIdxList = new ArrayList<>();
        for (Orders orders : thisMonthOrderList) {
            // 이번 달에 생성된 발주서 중 결제 안된 것들
            HeadIncomeDto.FindHeadIncomeRes resDto = headIncomeService.findByOrdersIdx(orders.getIdx());
            if (!resDto.isPaid()) {
                totalPrice += resDto.getPrice();
                notPaidHeadIncomeIdxList.add(resDto.getIdx());
            }
        }

        SettlementDto.PayReq reqDto = SettlementDto.PayReq.builder()
                .paymentPrice((long)totalPrice)
                .headIncomeidxList(notPaidHeadIncomeIdxList)
                .build();


        // verify 전 단계 완료
        Long settlementIdx = settlementService.pay(storeIdx, reqDto);


        // 모든 미납 내역
//        List<HeadIncomeDto.UnpaidRes> unpaidResDtoList = headIncomeService.findUnpaidSettlement(storeIdx);
//
//        int totalPrice = 0;
//
//        for (HeadIncomeDto.UnpaidRes unpaidRes : unpaidResDtoList) {
//            totalPrice += unpaidRes.getPrice();
//        }






        return ResponseEntity.ok("");
    }

    @PostMapping("/verify")
    public ResponseEntity verify (@AuthenticationPrincipal AuthUserDetails authUserDetails
            ,@RequestBody SettlementDto.VerifyReq verifyReq) {
        System.out.println(verifyReq.getPaymentIdx());

        settlementService.verify(authUserDetails, verifyReq);
        return ResponseEntity.ok("성공");
    }


}
