package com.example.batch.domain.delivery;

import com.example.batch.domain.delivery.model.Delivery;
import com.example.batch.domain.orders.model.Orders;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Delivery d WHERE d.orders = :orders")
    Delivery findByOrdersForUpdate(@Param("orders") Orders orders);
}
