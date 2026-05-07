package com.example.nexus.domain.head;

import com.example.nexus.domain.head.model.HeadIncome;
import com.example.nexus.domain.head.model.HeadIncomeDto;
import com.example.nexus.domain.orders.model.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HeadIncomeService {

    private final HeadIncomeRepository headIncomeRepository;

//    public List<HeadIncomeDto.UnpaidRes> findUnpaidSettlement (Long storeIdx) {
//        List<HeadIncomeDto.UnpaidRes> unpaidResList = new ArrayList<>();
//        List<HeadIncome> headIncomeList = headIncomeRepository.findAllByStoreIdx(storeIdx);
//
//        for (HeadIncome headIncome : headIncomeList) {
//            if (!headIncome.getStatus()) {
//                HeadIncomeDto.UnpaidRes resDto = HeadIncomeDto.UnpaidRes.builder()
//                        .idx(headIncome.getIdx())
//                        .status(headIncome.getStatus())
//                        .price(headIncome.getPrice())
//                        .storeIdx(headIncome.getStore().getIdx())
//                        .build();
//                unpaidResList.add(resDto);
//            }
//        }
//        return unpaidResList;
//    }

    // ordersIdx 로 HeadIncome을 찾기
    public HeadIncomeDto.FindHeadIncomeRes findByOrdersIdx(Long ordersIdx) {
        HeadIncome headIncome = headIncomeRepository.findByOrdersIdx(ordersIdx);
        if (headIncome != null) {
            HeadIncomeDto.FindHeadIncomeRes resDto = HeadIncomeDto.FindHeadIncomeRes.builder()
                    .idx(headIncome.getIdx())
                    .price(headIncome.getPrice())
                    .paid(headIncome.getStatus())
                    .settlementIdx(headIncome.getSettlementIdx())
                    .storeIdx(headIncome.getStore().getIdx())
                    .ordersIdx(headIncome.getOrders().getIdx())
                    .build();
            return resDto;
        }
        return null;
    }



}
