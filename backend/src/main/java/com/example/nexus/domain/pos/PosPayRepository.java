package com.example.nexus.domain.pos;

import com.example.nexus.domain.pos.model.PosPay;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PosPayRepository extends JpaRepository<PosPay, Long> {
    List<PosPay> findByStore_IdxAndPaidAtBetweenOrderByPaidAtDesc(Long storeIdx, LocalDateTime from, LocalDateTime to);

    // 대시보드
    @Query("SELECT COALESCE(SUM(p.payAmount), 0) FROM PosPay p WHERE p.store.idx = :storeIdx AND p.paidAt BETWEEN :from AND :to")
    long sumPayAmountByStoreAndPaidAtBetween(@Param("storeIdx") Long storeIdx, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
