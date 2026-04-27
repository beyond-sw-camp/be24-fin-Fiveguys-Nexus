package com.example.nexus.posorderitem;

import com.example.nexus.posorderitem.model.PosOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosOrderItemRepository extends JpaRepository<PosOrderItem, Long> {
}
