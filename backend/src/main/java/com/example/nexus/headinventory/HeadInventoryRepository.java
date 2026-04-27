package com.example.nexus.headinventory;

import com.example.nexus.headinventory.model.HeadInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeadInventoryRepository extends JpaRepository<HeadInventory, Long> {
}
