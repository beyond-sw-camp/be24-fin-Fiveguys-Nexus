package com.example.nexus.domain.pos;

import com.example.nexus.domain.pos.model.PosOrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PosOrdersItemRepository extends JpaRepository<PosOrdersItem, Long> {
    List<PosOrdersItem> findByPosPay_IdxIn(List<Long> payIdxList);
}
