package com.example.nexus.domain.inventory;

import com.example.nexus.domain.inventory.model.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
}
