package com.example.nexus.posstoreinventory;

import com.example.nexus.posstoreinventory.model.PosStoreInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosStoreInventoryRepository extends JpaRepository<PosStoreInventory, Long> {
}
