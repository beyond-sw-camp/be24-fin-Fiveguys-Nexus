package com.example.nexus.domain.orders;

import com.example.nexus.common.enums.OrdersStatus;
import com.example.nexus.domain.orders.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {
    List<Orders> findAllByStore_IdxAndOrdersStatus(Long storeIdx, OrdersStatus orderStatus);
    List<Orders> findAllByStore_IdxAndOrdersStatusIn(Long storeIdx, List<OrdersStatus> ordersStatuses);
    List<Orders> findAllByOrdersStatus(OrdersStatus ordersStatus);
    List<Orders> findAllByIsDangerTrue();

    @Query("SELECT COALESCE(SUM(i.count), 0) / GREATEST(COUNT(DISTINCT o.idx), 1) " +
            "FROM Orders o JOIN o.ordersItemList i " +
            "WHERE o.store.idx = :storeIdx AND o.createdAt >= :since")
    Integer findAvgQtyByStoreAndPeriod(@Param("storeIdx") Long storeIdx, @Param("since") LocalDateTime since);
}
