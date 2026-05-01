package com.example.nexus.domain.head;

import com.example.nexus.domain.head.model.HeadInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HeadInventoryRepository extends JpaRepository<HeadInventory, Long> {
    Optional<HeadInventory> findByProductIdx(Long productIdx);
}
