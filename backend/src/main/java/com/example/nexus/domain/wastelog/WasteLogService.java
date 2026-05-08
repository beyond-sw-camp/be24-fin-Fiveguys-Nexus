package com.example.nexus.domain.wastelog;

import com.example.nexus.common.enums.InventoryStatus;
import com.example.nexus.domain.pos.PosStoreInventoryRepository;
import com.example.nexus.domain.pos.model.PosStoreInventory;
import com.example.nexus.domain.store.StoreInventoryRepository;
import com.example.nexus.domain.store.StoreRepository;
import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.store.model.StoreInventory;
import com.example.nexus.domain.wastelog.model.WasteLog;
import com.example.nexus.domain.wastelog.model.WasteLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WasteLogService {

    private final WasteLogRepository wasteLogRepository;
    private final StoreRepository storeRepository;
    private final PosStoreInventoryRepository posStoreInventoryRepository;
    private final StoreInventoryRepository storeInventoryRepository;

    @Transactional
    public WasteLogDto.WasteRes createWaste(Long userIdx, WasteLogDto.PosWasteReq req) {
        Store store = storeRepository.findByUserIdx(userIdx).orElseThrow();

        PosStoreInventory posLot = posStoreInventoryRepository.findById(req.getPosStoreInventoryIdx()).orElseThrow();

        StoreInventory hqLot = storeInventoryRepository.findByStore_IdxAndProduct_IdxAndManufacturedDate(store.getIdx(), posLot.getProduct().getIdx(), posLot.getManufacturedDate()).orElseThrow();

        posLot.setCount(posLot.getCount() - req.getQuantity());
        hqLot.setCount(hqLot.getCount() - req.getQuantity());

        posLot.setStatus(resolveStatus(posLot.getCount(), posLot.getProduct().getMinStock()));
        hqLot.setStatus(resolveStatus(hqLot.getCount(), hqLot.getProduct().getMinStock()));

        long amountLoss = (long) req.getQuantity() * posLot.getProduct().getUnitPrice();

        WasteLog wasteLog = new WasteLog();
        wasteLog.setQuantity(req.getQuantity());
        wasteLog.setAmountLoss(amountLoss);
        wasteLog.setWasteDate(LocalDateTime.now());
        wasteLog.setWasteReason(req.getWasteReason());
        wasteLog.setStore(store);
        wasteLog.setProduct(posLot.getProduct());

        return WasteLogDto.WasteRes.from(wasteLogRepository.save(wasteLog));
    }

    private InventoryStatus resolveStatus(int count, int minStock) {
        if (count <= 0) return InventoryStatus.CRITICAL;
        // TODO: 유통기한 임박 상태 변경 로직 수정 필요
        if (count <= minStock / 2) return InventoryStatus.CRITICAL;
        if (count <= minStock) return InventoryStatus.LOW;

        return InventoryStatus.NORMAL;
    }
}
