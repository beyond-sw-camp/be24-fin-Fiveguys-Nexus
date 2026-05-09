package com.example.nexus.domain.store;

import com.example.nexus.common.exception.BaseException;
import com.example.nexus.common.model.BaseResponseStatus;
import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.store.model.StoreIncomeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreIncomeService {

    private final StoreRepository storeRepository;
    private final StoreIncomeRepository storeIncomeRepository;

    @Transactional(readOnly = true)
    public StoreIncomeDto.MonthlyIncomeRes getMonthlyIncome(Long userIdx) {
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.STORE_NOT_FOUND));

        List<Object[]> rows = storeIncomeRepository.findMonthlyIncomeSumByStoreIdx(store.getIdx());

        List<StoreIncomeDto.MonthlyIncomeItemRes> monthlyIncomes = rows.stream()
                .map(row -> StoreIncomeDto.MonthlyIncomeItemRes.builder()
                        .year(((Number) row[0]).intValue())
                        .month(((Number) row[1]).intValue())
                        .totalAmount(((Number) row[2]).longValue())
                        .build())
                .toList();

        return StoreIncomeDto.MonthlyIncomeRes.builder()
                .storeIdx(store.getIdx())
                .storeName(store.getStoreName())
                .monthlyIncomes(monthlyIncomes)
                .build();
    }
}