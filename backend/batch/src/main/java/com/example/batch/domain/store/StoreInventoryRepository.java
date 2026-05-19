package com.example.batch.domain.store;

import com.example.batch.domain.store.model.StoreInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreInventoryRepository extends JpaRepository<StoreInventory, Long> {
}
