package com.example.nexus.domain.head;

import com.example.nexus.domain.head.model.HeadIncome;
import com.example.nexus.domain.head.model.HeadIncomeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HeadIncomeService {

    private final HeadIncomeRepository headIncomeRepository;

    public List<HeadIncomeDto.UnpaidRes> findUnpaidSettlement (Long storeIdx) {
        List<HeadIncomeDto.UnpaidRes> unpaidResList = new ArrayList<>();
        List<HeadIncome> headIncomeList = headIncomeRepository.findAllByStoreIdx(storeIdx);

        for (HeadIncome headIncome : headIncomeList) {
            if (!headIncome.getStatus()) {
                HeadIncomeDto.UnpaidRes resDto = HeadIncomeDto.UnpaidRes.builder()
                        .idx(headIncome.getIdx())
                        .status(headIncome.getStatus())
                        .price(headIncome.getPrice())
                        .storeIdx(headIncome.getStore().getIdx())
                        .build();
                unpaidResList.add(resDto);
            }
        }
        return unpaidResList;
    }

}
