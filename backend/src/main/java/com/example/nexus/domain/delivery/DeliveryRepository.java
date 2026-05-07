package com.example.nexus.domain.delivery;

import com.example.nexus.common.enums.DeliveryStatus;
import com.example.nexus.domain.delivery.model.Delivery;
import com.example.nexus.domain.orders.model.Orders;
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

    Delivery findByIdx(Long deliveryIdx);

    Delivery findByOrders(Orders orders);

    // 배송 상태 자동 전환용 - 특정 상태 + 출발일이 기준 시간 이전인 배송 조회
    List<Delivery> findByDeliveryStatusAndDepartureDateBefore(DeliveryStatus status, LocalDateTime before);

    // 배송 지연 자동 감지용 - 예상도착일 초과 + 미완료 배송 조회
    @Query("SELECT d FROM Delivery d JOIN FETCH d.orders o JOIN FETCH o.store s " +
            "WHERE d.estimatedArrivalAt < :now " +
            "AND d.deliveryStatus NOT IN (:excludeStatuses)")
    List<Delivery> findOverdueDeliveries(
            @Param("now") LocalDateTime now,
            @Param("excludeStatuses") List<DeliveryStatus> excludeStatuses);

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

    // 본인 가맹점 배송 현황 조회
    @Query("SELECT d FROM Delivery d JOIN FETCH d.orders o JOIN FETCH o.store s WHERE s.idx = :storeIdx")
    List<Delivery> findAllByOrdersStoreIdx(@Param("storeIdx") Long storeIdx);

    // 가맹점 배송 현황 필터 및 발주번호 검색 조회
    @Query("SELECT d FROM Delivery d " +
            "JOIN FETCH d.orders o " +
            "JOIN FETCH o.store s " +
            "WHERE s.idx = :storeIdx " +
            "AND (:orderIdx IS NULL OR o.idx = :orderIdx) " +
            "AND (:status IS NULL OR d.deliveryStatus = :status) " +
            "AND (:year IS NULL OR FUNCTION('YEAR', d.departureDate) = :year) " +
            "AND (:month IS NULL OR FUNCTION('MONTH', d.departureDate) = :month) " +
            "AND (:day IS NULL OR FUNCTION('DAY', d.departureDate) = :day)")
    List<Delivery> findAllByStoreFilters(
            @Param("storeIdx") Long storeIdx,
            @Param("orderIdx") Long orderIdx,
            @Param("status") DeliveryStatus status,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("day") Integer day);
}

