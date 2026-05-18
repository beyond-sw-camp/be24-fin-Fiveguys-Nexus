package com.example.statistics;

import com.example.statistics.domain.pos.model.PosPay;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<PosPay, Long> {

    @Query("SELECT COALESCE(SUM(p.payAmount), 0) FROM PosPay p WHERE p.paidAt >= :start AND p.paidAt < :end")
    long sumTodaySales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p.store.idx, p.store.storeName, SUM(p.payAmount) " +
            "FROM PosPay p " +
            "WHERE p.paidAt >= :start AND p.paidAt < :end " +
            "GROUP BY p.store.idx, p.store.storeName " +
            "ORDER BY SUM(p.payAmount) DESC")
    List<Object[]> findTopStores(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

    @Query("SELECT m.idx, m.menuName, SUM(poi.quantity) " +
            "FROM PosOrdersItem poi " +
            "JOIN poi.menu m " +
            "JOIN poi.posPay p " +
            "WHERE p.paidAt >= :start AND p.paidAt < :end " +
            "GROUP BY m.idx, m.menuName " +
            "ORDER BY SUM(poi.quantity) DESC")
    List<Object[]> findTopMenus(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

    @Query("SELECT HOUR(p.paidAt), COALESCE(SUM(p.payAmount), 0) " +
            "FROM PosPay p " +
            "WHERE p.paidAt >= :start AND p.paidAt < :end " +
            "GROUP BY HOUR(p.paidAt) " +
            "ORDER BY HOUR(p.paidAt)")
    List<Object[]> findHourlySales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT mc.menuCategoryName, COALESCE(SUM(poi.quantity * m.price), 0) " +
            "FROM PosOrdersItem poi " +
            "JOIN poi.menu m " +
            "JOIN m.menuCategory mc " +
            "JOIN poi.posPay p " +
            "WHERE p.paidAt >= :start AND p.paidAt < :end " +
            "GROUP BY mc.menuCategoryName " +
            "ORDER BY SUM(poi.quantity * m.price) DESC")
    List<Object[]> findSalesByCategory(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p.method, COALESCE(SUM(p.payAmount), 0) " +
            "FROM PosPay p " +
            "WHERE p.paidAt >= :start AND p.paidAt < :end " +
            "GROUP BY p.method")
    List<Object[]> findSalesByPayMethod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}