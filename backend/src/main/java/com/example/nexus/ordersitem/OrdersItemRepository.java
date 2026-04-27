package com.example.nexus.ordersitem;

import com.example.nexus.ordersitem.model.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long> {
}
