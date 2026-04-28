package com.example.nexus.domain.pos;

import com.example.nexus.domain.pos.model.PosStoreInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosStoreInventoryRepository extends JpaRepository<PosStoreInventory, Long> {
}
