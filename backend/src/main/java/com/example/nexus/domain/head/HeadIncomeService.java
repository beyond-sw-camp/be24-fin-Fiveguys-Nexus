package com.example.nexus.domain.head;

import com.example.nexus.domain.head.model.HeadIncome;
import com.example.nexus.domain.head.model.HeadIncomeDto;
import com.example.nexus.domain.orders.model.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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


    public List<HeadIncomeDto.ListRes> getIncomeList(
            String storeName, Boolean status, LocalDate startDate, LocalDate endDate) {

        List<HeadIncome> incomes = headIncomeRepository.findByFilters(storeName, status, startDate, endDate);

        List<HeadIncomeDto.ListRes> result = new ArrayList<>();

        for (HeadIncome income : incomes) {

            LocalDate orderDate = LocalDate.now();
            if (income.getOrders() != null && income.getOrders().getCreatedAt() != null) {
                orderDate = income.getOrders().getCreatedAt().toLocalDate();
            }

            HeadIncomeDto.ListRes dto = HeadIncomeDto.ListRes.builder()
                    .headIncomeIdx(income.getIdx())
                    .storeIdx(income.getStore().getIdx())
                    .storeName(income.getStore().getStoreName())
                    .periodStart(orderDate.withDayOfMonth(1))
                    .periodEnd(orderDate.withDayOfMonth(orderDate.lengthOfMonth()))
                    .totalPrice(income.getPrice())
                    .status(income.getStatus())
                    .settlementIdx(income.getSettlementIdx())
                    .orderCount(1)
                    .build();

            result.add(dto);
        }

        return result;
    }

    public HeadIncome findById(Long idx) {
        return headIncomeRepository.findById(idx).orElse(null);
    }
}
