package com.example.nexus.domain.head;

import com.example.nexus.domain.head.model.HeadInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeadInventoryRepository extends JpaRepository<HeadInventory, Long> {
}
