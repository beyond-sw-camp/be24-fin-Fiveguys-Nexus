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

        // 로그인 한 사용자의 지점 번호 받아오기
        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());

        // 지금 기준 2026년 5월이므로 2026년 4월 을 lastMonth에 저장
        YearMonth lastMonth = YearMonth.from(LocalDateTime.now()).minusMonths(1);

        // storeIdx로 모든 주문 찾기
        List<Orders> ordersList = ordersService.findByStoreIdx(storeIdx);

        // 지불하지 않은 HeadIncome 저장할 리스트 만들거임
        List<HeadIncomeDto.FindHeadIncomeRes> unpaidList = new ArrayList<>();

        // 처음에 settlementIdx를 0으로 초기화 하고
        Long settlementIdx = 0L;
        
        // store의 발주서를 돌면서
        for (Orders order : ordersList) {

            // 각 주문서 마다 년 월을 찾아서
            YearMonth orderYearMonth = YearMonth.from(order.getCreatedAt());

            // 지난 달 년 월과 같다면
            if (lastMonth.equals(orderYearMonth)) {
                // orders 테이블의 idx로 dto 생성
                HeadIncomeDto.FindHeadIncomeRes resDto = headIncomeService.findByOrdersIdx(order.getIdx());

                // !(지난 달 중 지불했거나 검색 결과가 없다면)
                if (resDto != null && !resDto.isPaid()) {
                    // 결제해야 할 발주서 목록에 넣기
                    unpaidList.add(resDto);
                    
                    // null 이라서 가져올 수가 없음
//                    settlementIdx = resDto.getSettlementIdx();
                }
            }
        }

        // 결제 해야 하는 발주서 리스트 응답 dto 로 만들어서 보내줌
        SettlementDto.unPaid dto = SettlementDto.unPaid.builder()

//                .settlementIdx(settlementIdx)
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
        System.out.println(dto.getSettlementIdx());
        System.out.println(dto.getPaymentId());
        // dto 에서 1이 줄어들어서 들어옴
        settlementService.verify(authUserDetails, dto, lastMonth);
        return ResponseEntity.ok("결제 성공");
    }


}
