package com.example.nexus.domain.pos;

import com.example.nexus.domain.pos.model.PosStoreInventory;
import com.example.nexus.domain.pos.model.PosStoreInventoryDto;
import com.example.nexus.domain.store.StoreInventoryRepository;
import com.example.nexus.domain.store.StoreRepository;
import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.store.model.StoreInventory;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PosService {
    private final PosStoreInventoryRepository posStoreInventoryRepository;
    private final StoreRepository storeRepository;

    public List<PosStoreInventoryDto.ListRes> listByUserIdx(Long idx) {
        Store store = storeRepository.findByUserIdx(idx).orElseThrow();

        Long storeIdx = store.getIdx();

        List<PosStoreInventory> inventoryList = posStoreInventoryRepository.findByStoreIdx(storeIdx);

        return inventoryList.stream().map(PosStoreInventoryDto.ListRes::from).toList();
    }
}
