package com.example.nexus.domain.pos;

import com.example.nexus.domain.pos.model.PosPay;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PosPayRepository extends JpaRepository<PosPay, Long> {
    List<PosPay> findByStoreIdxAndPaidAtBetweenOrderByPaidAtDesc(Long storeIdx, LocalDateTime from, LocalDateTime to);

    List<PosPay> findByStoreIdxAndPaidAtBetweenAndStoreInventoryDeductedAtIsNullOrderByPaidAtAsc(Long storeIdx, LocalDateTime from, LocalDateTime to);

    // 대시보드: 반개방 구간 [from, to)
    @Query("SELECT COALESCE(SUM(p.payAmount), 0) FROM PosPay p WHERE p.store.idx = :storeIdx AND p.paidAt >= :from AND p.paidAt < :to")
    long sumPayAmountByStoreAndPeriod(@Param("storeIdx") Long storeIdx, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // 대시보드: 기간 내 일별 매출 합산 (날짜, 금액)
    @Query("SELECT CAST(p.paidAt AS LocalDate), COALESCE(SUM(p.payAmount), 0) FROM PosPay p WHERE p.store.idx = :storeIdx AND p.paidAt >= :from AND p.paidAt < :to GROUP BY CAST(p.paidAt AS LocalDate) ORDER BY CAST(p.paidAt AS LocalDate)")
    List<Object[]> findDailySalesByStoreAndPeriod(@Param("storeIdx") Long storeIdx, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
