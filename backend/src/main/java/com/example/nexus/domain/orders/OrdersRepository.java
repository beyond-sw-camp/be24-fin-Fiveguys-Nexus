package com.example.nexus.domain.orders;

import com.example.nexus.common.enums.OrdersType;
import com.example.nexus.domain.orders.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findAllByOrdersType(OrdersType ordersType);
}
