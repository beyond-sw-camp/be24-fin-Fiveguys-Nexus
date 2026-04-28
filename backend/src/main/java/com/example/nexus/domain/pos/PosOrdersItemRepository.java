package com.example.nexus.domain.pos;

import com.example.nexus.domain.pos.model.PosOrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosOrdersItemRepository extends JpaRepository<PosOrdersItem, Long> {
}
