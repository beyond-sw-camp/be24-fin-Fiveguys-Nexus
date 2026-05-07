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


    @PostMapping("/pay")
    public ResponseEntity pay (@AuthenticationPrincipal AuthUserDetails authUserDetails) {

        // 로그인 한 사용자의  storeIdx 가져오기
        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());

        LocalDateTime now = LocalDateTime.now();
        YearMonth currentYearMonth = YearMonth.from(now);

        List<Orders> ordersList = ordersService.findByStoreIdx(storeIdx);

        List<Orders> lastMonthOrderList = new ArrayList<>();

        YearMonth lastMonth = currentYearMonth.minusMonths(1);
        for (Orders orders : ordersList) {

            YearMonth orderYearMonth = YearMonth.from(orders.getCreatedAt());

            if (lastMonth.equals(orderYearMonth)) {
                // 이번 달에 결제/생성된 주문 처리 로직 작성

                lastMonthOrderList.add(orders);
            }
        }

        // 여기까지는 지난 달 발주서 제대로 불러왔음

        int totalPrice = 0;
        List<Long> notPaidHeadIncomeIdxList = new ArrayList<>();
        for (Orders orders : lastMonthOrderList) {
            HeadIncomeDto.FindHeadIncomeRes resDto = headIncomeService.findByOrdersIdx(orders.getIdx());
            if (resDto != null && !resDto.isPaid()) {
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


        return ResponseEntity.ok("");
    }

    @PostMapping("/verify")
    public ResponseEntity verify (@AuthenticationPrincipal AuthUserDetails authUserDetails) {

        LocalDateTime now = LocalDateTime.now();

        YearMonth currentYearMonth = YearMonth.from(now);

        YearMonth lastMonth = currentYearMonth.minusMonths(1);

        System.out.println(lastMonth);

//        settlementService.verify(authUserDetails, );
        return ResponseEntity.ok("성공");
    }


}
