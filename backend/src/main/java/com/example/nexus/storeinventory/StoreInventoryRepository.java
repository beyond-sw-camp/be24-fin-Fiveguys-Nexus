package com.example.nexus.storeinventory;

import com.example.nexus.storeinventory.model.StoreInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreInventoryRepository extends JpaRepository<StoreInventory, Long> {
}
