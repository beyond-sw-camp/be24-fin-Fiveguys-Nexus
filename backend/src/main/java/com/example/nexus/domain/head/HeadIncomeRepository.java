package com.example.nexus.domain.head;

import com.example.nexus.domain.head.model.HeadIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface HeadIncomeRepository extends JpaRepository<HeadIncome, Long> {
    List<HeadIncome> findAllByStoreIdx(Long storeIdx);

    List<HeadIncome> findBySettlementIdx(Long idx);

    HeadIncome findByOrdersIdx(Long orderIdx);

    @Query("SELECT hi FROM HeadIncome hi " +
            "JOIN hi.orders o " +
            "WHERE (:storeName IS NULL OR hi.store.storeName LIKE %:storeName%) " +
            "AND (:status IS NULL OR hi.status = :status) " +
            "AND (:startDate IS NULL OR FUNCTION('DATE', o.createdAt) >= :startDate) " +
            "AND (:endDate IS NULL OR FUNCTION('DATE', o.createdAt) <= :endDate)")
    List<HeadIncome> findByFilters(String storeName, Boolean status, LocalDate startDate, LocalDate endDate);
}
