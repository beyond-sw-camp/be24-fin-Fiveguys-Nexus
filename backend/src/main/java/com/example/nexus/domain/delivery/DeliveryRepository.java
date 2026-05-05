package com.example.nexus.domain.delivery;

import com.example.nexus.common.enums.DeliveryStatus;
import com.example.nexus.domain.delivery.model.Delivery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    // 대시보드용 - 본사 대시보드 진행중 배송 건수 KPI (여러 상태 합산)
    long countByDeliveryStatusIn(List<DeliveryStatus> statuses);

    // 대시보드용 - 본사 대시보드 특정 상태 배송 건수
    long countByDeliveryStatus(DeliveryStatus status);

    // 대시보드용 - 본사 대시보드 배송 비율 계산 (최근 한달 상태별 건수)
    long countByDeliveryStatusAndDepartureDateAfter(DeliveryStatus status, LocalDateTime since);

    // 대시보드용 - 본사 대시보드 지연 배송 전체 목록 조회
    List<Delivery> findAllByDeliveryStatus(DeliveryStatus status);

    // 대시보드용 - 본사 대시보드 지연 배송 목록 무한스크롤 페이징
    Slice<Delivery> findByDeliveryStatus(DeliveryStatus status, Pageable pageable);

    // 점주 대시보드용 - 나의 배송 현황 목록 (배송완료 제외, 최신순)
    List<Delivery> findByOrders_Store_IdxAndDeliveryStatusNotOrderByDepartureDateDesc(Long storeIdx, DeliveryStatus excludeStatus);

    List<Delivery> findByDeliveryStatus(DeliveryStatus status);

    // 본사 배송 조회용
    @Query("SELECT d FROM Delivery d " +
            "JOIN FETCH d.orders o " +
            "JOIN FETCH o.store s " +
            "WHERE (:storeName IS NULL OR s.storeName LIKE %:storeName%) " +
            "AND (:status IS NULL OR d.deliveryStatus = :status) " +
            "AND (:year IS NULL OR FUNCTION('YEAR', d.departureDate) = :year) " +
            "AND (:month IS NULL OR FUNCTION('MONTH', d.departureDate) = :month) " +
            "AND (:day IS NULL OR FUNCTION('DAY', d.departureDate) = :day)")
    List<Delivery> findAllByFilters(
            @Param("storeName") String storeName,
            @Param("status") DeliveryStatus status,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("day") Integer day);
}
