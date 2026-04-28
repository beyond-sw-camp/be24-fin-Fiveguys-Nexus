package com.example.nexus.domain.store;

import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.store.model.StoreInventory;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreInventoryRepository storeInventoryRepository;

    public List<StoreInventoryDto.ListRes> listByUserIdx(Long userIdx) {
        Store store = storeRepository.findByUserIdx(userIdx).orElseThrow();

        Long storeIdx = store.getIdx();

        List<StoreInventory> inventoryList = storeInventoryRepository.findByStoreIdx(storeIdx);
        return inventoryList.stream().map(StoreInventoryDto.ListRes::from).toList();
    }
}
