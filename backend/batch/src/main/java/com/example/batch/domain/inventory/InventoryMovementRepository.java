package com.example.batch.domain.inventory;

import com.example.batch.domain.inventory.model.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
}
