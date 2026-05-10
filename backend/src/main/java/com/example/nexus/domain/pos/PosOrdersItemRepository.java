package com.example.nexus.domain.pos;

import com.example.nexus.domain.pos.model.PosOrdersItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PosOrdersItemRepository extends JpaRepository<PosOrdersItem, Long> {
    List<PosOrdersItem> findByPosPay_IdxIn(List<Long> payIdxList);

    // AI 보고서: N+1 문제 없는 인기 메뉴 통계 (수량 quantity 합산)
    @Query("SELECT m.menuName " +
            "FROM PosOrdersItem poi " +
            "JOIN poi.menu m " +
            "JOIN poi.posPay p " +
            "WHERE p.paidAt BETWEEN :startDate AND :endDate " +
            "GROUP BY m.menuName " +
            "ORDER BY SUM(poi.quantity) DESC")
    List<String> findTopSellingMenus(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // 2. 카테고리별 매출 비중 (수익성 분석용)
    // 반환값: [카테고리명, 판매량합계]
    @Query("SELECT m.menuCategory, SUM(poi.quantity) " +
            "FROM PosOrdersItem poi JOIN poi.menu m JOIN poi.posPay p " +
            "WHERE p.paidAt BETWEEN :start AND :end " +
            "GROUP BY m.menuCategory")
    List<Object[]> findCategorySales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
