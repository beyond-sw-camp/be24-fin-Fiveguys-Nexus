package com.example.nexus.domain.orders;

import com.example.nexus.domain.orders.model.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long> {
}
