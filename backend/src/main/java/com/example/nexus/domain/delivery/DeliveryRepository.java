package com.example.nexus.domain.delivery;

import com.example.nexus.common.enums.DeliveryStatus;
import com.example.nexus.domain.delivery.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    // 대시보드용
    long countByDeliveryStatusIn(List<DeliveryStatus> statuses);

    long countByDeliveryStatus(DeliveryStatus status);

    List<Delivery> findAllByDeliveryStatus(DeliveryStatus status);
}
