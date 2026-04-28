package com.example.nexus.domain.orders;

import com.example.nexus.domain.orders.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}
