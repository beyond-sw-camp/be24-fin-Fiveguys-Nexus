package com.example.statistics.domain.pos;

import com.example.statistics.domain.pos.model.PosOrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosOrdersItemRepository extends JpaRepository<PosOrdersItem, Long> {
}