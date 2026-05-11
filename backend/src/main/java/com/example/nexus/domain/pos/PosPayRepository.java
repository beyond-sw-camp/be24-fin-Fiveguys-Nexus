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

    // AI 보고서: 특정 기간 총 매출 (주간 매출 계산용)
    @Query("SELECT SUM(p.payAmount) FROM PosPay p WHERE p.paidAt BETWEEN :start AND :end")
    Long sumSalesByDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // AI 보고서: 시간대별 매출 및 건수 (피크타임 분석용)
    // 반환값: [시간(0~23), 결제건수, 매출합계]
    @Query("SELECT HOUR(p.paidAt) as hour, COUNT(p) as count, SUM(p.payAmount) as total " +
            "FROM PosPay p " +
            "WHERE p.paidAt BETWEEN :start AND :end " +
            "GROUP BY HOUR(p.paidAt) " +
            "ORDER BY hour")
    List<Object[]> findHourlySales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}