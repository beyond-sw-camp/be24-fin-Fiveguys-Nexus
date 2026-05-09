package com.example.nexus.domain.store;

import com.example.nexus.domain.pos.model.PosPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreIncomeRepository extends JpaRepository<PosPay, Long> {


    @Query("""
            SELECT YEAR(p.paidAt), MONTH(p.paidAt), SUM(p.payAmount)
            FROM PosPay p
            WHERE p.store.idx = :storeIdx
            GROUP BY YEAR(p.paidAt), MONTH(p.paidAt)
            ORDER BY YEAR(p.paidAt) ASC, MONTH(p.paidAt) ASC
            """)
    List<Object[]> findMonthlyIncomeSumByStoreIdx(@Param("storeIdx") Long storeIdx);
}