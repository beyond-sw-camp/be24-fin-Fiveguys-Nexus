package com.example.nexus.domain.orders;

import com.example.nexus.common.enums.OrdersStatus;
import com.example.nexus.common.enums.OrdersType;
import com.example.nexus.domain.orders.model.Orders;
import com.example.nexus.domain.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findAllByOrdersTypeAndOrdersStatus(OrdersType ordersType, OrdersStatus ordersStatus);
    List<Orders> findAllByStore_IdxAndOrdersStatus(Long storeIdx, OrdersStatus orderStatus);
    List<Orders> findAllByOrdersStatus(OrdersStatus ordersStatus);
}
