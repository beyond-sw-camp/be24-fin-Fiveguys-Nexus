package com.example.nexus.domain.pos;

import com.example.nexus.domain.pos.model.PosStoreInventory;
import com.example.nexus.domain.store.model.StoreInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PosStoreInventoryRepository extends JpaRepository<PosStoreInventory, Long> {
    List<PosStoreInventory> findByStoreIdx(Long storeIdx);

    List<PosStoreInventory> findByStore_IdxAndProduct_IdxOrderByManufacturedDateAsc(Long storeIdx, Long productIdx);
}
