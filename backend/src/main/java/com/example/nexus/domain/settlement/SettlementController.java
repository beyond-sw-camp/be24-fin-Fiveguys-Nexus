package com.example.nexus.domain.settlement;

import com.example.nexus.domain.head.HeadIncomeService;
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

    @GetMapping("/settlement/list")
    public ResponseEntity findSettlement(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        LocalDateTime now = LocalDateTime.now();
        YearMonth currentYearMonth = YearMonth.from(now);

        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());
        List<Orders> ordersList = ordersService.findByStoreIdx(storeIdx);
        System.out.println(ordersList.size());
        List<Orders> thisMonthOrderList = new ArrayList<>();

        for (Orders orders : ordersList) {

            YearMonth orderYearMonth = YearMonth.from(orders.getCreatedAt());

            if (currentYearMonth.equals(orderYearMonth)) {
                // 이번 달에 결제/생성된 주문 처리 로직 작성

                // dto builder build로 해결하기
                thisMonthOrderList.add(orders);
            }
        }
        System.out.println(thisMonthOrderList.size());
        return ResponseEntity.ok("success");

    }


    @PostMapping("/pay")
    public ResponseEntity pay (@AuthenticationPrincipal AuthUserDetails authUserDetails) {

        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());

        List<HeadIncomeDto.UnpaidRes> unpaidResDtoList = headIncomeService.findUnpaidSettlement(storeIdx);

        int totalPrice = 0;

        for (HeadIncomeDto.UnpaidRes unpaidRes : unpaidResDtoList) {
            totalPrice += unpaidRes.getPrice();
        }

        return ResponseEntity.ok(totalPrice);
    }

    @PostMapping("/verify")
    public ResponseEntity verify (@AuthenticationPrincipal AuthUserDetails authUserDetails
            ,@RequestBody SettlementDto.VerifyReq verifyReq) {
        System.out.println(verifyReq.getPaymentIdx());

        settlementService.verify(authUserDetails, verifyReq);
        return ResponseEntity.ok("성공");
    }


}
