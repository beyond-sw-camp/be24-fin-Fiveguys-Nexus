package com.example.nexus.domain.settlement;

import com.example.nexus.domain.settlement.model.Settlement;
import com.example.nexus.domain.settlement.model.SettlementDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;

    public List<SettlementDto.PayRes> findByStoreIdx(SettlementDto.PayReq dto) {
        List<Settlement> settlementList = settlementRepository.findAllByStoreIdx(dto.getStoreIdx());
        List<SettlementDto.PayRes> payResList = new ArrayList<>();
        for (Settlement settlement : settlementList) {
            if(!settlement.getStatus()) {
                SettlementDto.PayRes resDto = SettlementDto.PayRes.builder()
                        .price(settlement.getPrice())
                        .status(settlement.getStatus())
                        .pgPaymentId(settlement.getPgPaymentId())
                        .storeIdx(dto.getStoreIdx())
                        .ordersIdx(settlement.getOrders().getIdx())
                        .build();
                payResList.add(resDto);
            }
        }
        return payResList;
    }
}
