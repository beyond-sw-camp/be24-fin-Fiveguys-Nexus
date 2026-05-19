package com.example.batch.domain.delivery;

import com.example.batch.domain.delivery.model.Delivery;
import com.example.batch.domain.orders.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Delivery findByOrders(Orders orders);
}
