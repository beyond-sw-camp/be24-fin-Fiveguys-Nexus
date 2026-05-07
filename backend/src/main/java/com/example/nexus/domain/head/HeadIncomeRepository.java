package com.example.nexus.domain.head;

import com.example.nexus.domain.head.model.HeadIncome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HeadIncomeRepository extends JpaRepository<HeadIncome, Long> {
    List<HeadIncome> findAllByStoreIdx(Long storeIdx);

    List<HeadIncome> findBySettlementIdx(Long idx);

    HeadIncome findByOrdersIdx(Long orderIdx);
}
