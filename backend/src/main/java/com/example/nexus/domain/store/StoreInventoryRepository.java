package com.example.nexus.domain.store;

import com.example.nexus.domain.store.model.StoreInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreInventoryRepository extends JpaRepository<StoreInventory, Long> {
}
