package com.example.nexus.domain.orders;

import com.example.nexus.domain.orders.model.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long> {

    List<OrdersItem> findByOrdersIdx(Long ordersIdx);
}
