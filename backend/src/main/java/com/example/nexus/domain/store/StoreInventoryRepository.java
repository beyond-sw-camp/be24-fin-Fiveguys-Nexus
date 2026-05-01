package com.example.nexus.domain.store;

import com.example.nexus.domain.store.model.StoreInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreInventoryRepository extends JpaRepository<StoreInventory, Long> {
    List<StoreInventory> findByStoreIdx(Long storeIdx);
    Optional<StoreInventory> findByStoreIdxAndProductIdx(Long storeIdx, Long productIdx);
}
