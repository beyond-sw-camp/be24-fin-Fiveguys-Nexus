package com.example.nexus.posordersitem;

import com.example.nexus.posordersitem.model.PosOrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosOrdersItemRepository extends JpaRepository<PosOrdersItem, Long> {
}
