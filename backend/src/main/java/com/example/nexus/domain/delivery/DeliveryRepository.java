package com.example.nexus.domain.delivery;

import com.example.nexus.common.enums.DeliveryStatus;
import com.example.nexus.domain.delivery.model.Delivery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    // 대시보드용
    long countByDeliveryStatusIn(List<DeliveryStatus> statuses);

    long countByDeliveryStatus(DeliveryStatus status);

    // 배송 비율용: 최근 한달 상태별 건수
    long countByDeliveryStatusAndDepartureDateAfter(DeliveryStatus status, LocalDateTime since);

    List<Delivery> findAllByDeliveryStatus(DeliveryStatus status);

    // 대시보드용: 지연 배송 목록 Slice 페이징
    Slice<Delivery> findByDeliveryStatus(DeliveryStatus status, Pageable pageable);
}
