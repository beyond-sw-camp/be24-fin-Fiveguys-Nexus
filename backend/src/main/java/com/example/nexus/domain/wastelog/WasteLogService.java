package com.example.nexus.domain.wastelog;

import com.example.nexus.domain.store.StoreInventoryRepository;
import com.example.nexus.domain.store.model.StoreInventory;
import com.example.nexus.domain.wastelog.model.WasteLog;
import com.example.nexus.domain.wastelog.model.WasteLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WasteLogService {

    private final WasteLogRepository wasteLogRepository;
    private final StoreInventoryRepository storeInventoryRepository;

    public List<WasteLogDto.RegRes> registerOverDueDateInventories(List<Long> idxList) {

        List<WasteLogDto.RegRes> resList = new ArrayList<>();

        for(Long idx : idxList) {
            StoreInventory inventory = storeInventoryRepository.findById(idx).orElse(null);
            WasteLog entity = WasteLog.builder()
                    .quantity(inventory.getCount())
                    .amountLoss(inventory.getProduct().getUnitPrice())
                    .wasteDate(inventory.getManufacturedDate().plusDays(Long.parseLong(inventory.getProduct().getDangerDays())))
                    .wasteReason("유통기한 만료")
                    .store(inventory.getStore())
                    .product(inventory.getProduct())
                    .build();
            wasteLogRepository.save(entity);

            resList.add(WasteLogDto.RegRes.from(entity));
        }

        return resList;
    }

}
