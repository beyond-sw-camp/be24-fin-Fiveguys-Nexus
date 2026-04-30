package com.example.nexus.domain.pos;

import com.example.nexus.domain.pos.model.PosStoreInventory;
import com.example.nexus.domain.pos.model.PosStoreInventoryDto;
import com.example.nexus.domain.store.StoreInventoryRepository;
import com.example.nexus.domain.store.StoreRepository;
import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.store.model.StoreInventory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PosService {
    private final PosStoreInventoryRepository posStoreInventoryRepository;
    private final StoreRepository storeRepository;
    private final StoreInventoryRepository storeInventoryRepository;

    public List<PosStoreInventoryDto.ListRes> listByUserIdx(Long idx) {
        Store store = storeRepository.findByUserIdx(idx).orElseThrow();

        Long storeIdx = store.getIdx();

        List<PosStoreInventory> inventoryList = posStoreInventoryRepository.findByStoreIdx(storeIdx);

        return inventoryList.stream().map(PosStoreInventoryDto.ListRes::from).toList();
    }

    public PosStoreInventoryDto.SyncCountRes changeCount(Long userIdx, Long posStoreInventoryIdx, Integer count) {
        Store myStore = storeRepository.findByUserIdx(userIdx).orElseThrow();

        PosStoreInventory posInventory = posStoreInventoryRepository.findById(posStoreInventoryIdx).orElseThrow();
        StoreInventory hqInventory = storeInventoryRepository.findById(posStoreInventoryIdx).orElseThrow();

        if (!posInventory.getStore().getIdx().equals(myStore.getIdx())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 매장 POS 재고만 수정할 수 있습니다.");
        }

        posInventory.setCount(count);
        hqInventory.setCount(count);

        PosStoreInventory posSaved = posStoreInventoryRepository.save(posInventory);
        StoreInventory hqSaved = storeInventoryRepository.save(hqInventory);

        return PosStoreInventoryDto.SyncCountRes.from(posSaved, hqSaved);
    }
}
