package com.example.nexus.domain.orders;

import com.example.nexus.common.enums.OrdersStatus;
import com.example.nexus.common.enums.OrdersType;
import com.example.nexus.domain.orders.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {
    List<Orders> findAllByStore_IdxAndOrdersStatus(Long storeIdx, OrdersStatus orderStatus);
    List<Orders> findAllByStore_IdxAndOrdersStatusIn(Long storeIdx, List<OrdersStatus> ordersStatuses);
    Page<Orders> findAllByStore_IdxAndOrdersStatusIn(Long storeIdx, List<OrdersStatus> ordersStatuses, Pageable pageable);
    List<Orders> findAllByOrdersStatus(OrdersStatus ordersStatus);
    List<Orders> findAllByIsDangerTrue();

    @Query("SELECT COALESCE(SUM(i.count), 0) / GREATEST(COUNT(DISTINCT o.idx), 1) " +
            "FROM Orders o JOIN o.ordersItemList i " +
            "WHERE o.store.idx = :storeIdx AND o.createdAt >= :since AND (:excludeIdx IS NULL OR o.idx <> :excludeIdx)")
    Integer findAvgQtyByStoreAndPeriod(@Param("storeIdx") Long storeIdx, @Param("since") LocalDateTime since, @Param("excludeIdx") Long excludeIdx);

    // 본사 대시보드용
    long countByOrdersTypeAndCreatedAtAfter(OrdersType ordersType, LocalDateTime since);

    long countByOrdersStatus(OrdersStatus ordersStatus);

    long countByIsDangerTrue();

    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m'), o.ordersStatus, COUNT(o) " +
            "FROM Orders o WHERE o.isDanger = true AND o.createdAt >= :since " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m'), o.ordersStatus " +
            "ORDER BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m')")
    List<Object[]> findDangerStatsByMonth(@Param("since") LocalDateTime since);

    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%m-%d'), COUNT(o) " +
            "FROM Orders o WHERE o.createdAt >= :since " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%m-%d') " +
            "ORDER BY FUNCTION('DATE_FORMAT', o.createdAt, '%m-%d')")
    List<Object[]> findWeeklyOrderStats(@Param("since") LocalDateTime since);

    // 점주 대시보드용
    long countByStore_IdxAndOrdersStatusAndOrdersType(Long storeIdx, OrdersStatus ordersStatus, OrdersType ordersType);

    List<Orders> findAllByStore_IdxAndOrdersStatusAndOrdersTypeOrderByCreatedAtDesc(Long storeIdx, OrdersStatus ordersStatus, OrdersType ordersType);

    @Query("SELECT COALESCE(SUM(o.price), 0) FROM Orders o WHERE o.store.idx = :storeIdx AND o.ordersStatus = 'APPROVE' AND o.createdAt >= :from AND o.createdAt < :to")
    long sumApprovedPriceByStoreAndPeriod(@Param("storeIdx") Long storeIdx, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
