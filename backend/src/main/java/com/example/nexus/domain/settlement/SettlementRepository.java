package com.example.nexus.domain.settlement;

import com.example.nexus.domain.settlement.model.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    List<Settlement> findAllByStoreIdx(Long storeIdx);
}
