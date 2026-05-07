package com.example.nexus.domain.orders;

import com.example.nexus.common.enums.OrdersStatus;
import com.example.nexus.common.enums.OrdersType;
import com.example.nexus.domain.orders.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {
    // 발주용 - 점주 제안 발주서 조회 (매장별 특정 상태)
    List<Orders> findAllByStore_IdxAndOrdersStatus(Long storeIdx, OrdersStatus orderStatus);

    // 발주용 - 점주 발주 이력 페이징 조회
    Page<Orders> findAllByStore_IdxAndOrdersStatusIn(Long storeIdx, List<OrdersStatus> ordersStatuses, Pageable pageable);

    // 발주용 - 본사 확정 발주 일괄 승인 대상 조회
    List<Orders> findAllByOrdersStatus(OrdersStatus ordersStatus);

    // 발주용 - 이상 발주 목록 조회 (위험 플래그 기준)
    List<Orders> findAllByIsDangerTrue();

    // 발주용 - 이상 발주 판별을 위한 매장별 평균 발주 수량 계산
    @Query("SELECT COALESCE(SUM(i.count), 0) / GREATEST(COUNT(DISTINCT o.idx), 1) " +
            "FROM Orders o JOIN o.ordersItemList i " +
            "WHERE o.store.idx = :storeIdx AND o.createdAt >= :since AND (:excludeIdx IS NULL OR o.idx <> :excludeIdx)")
    Integer findAvgQtyByStoreAndPeriod(@Param("storeIdx") Long storeIdx, @Param("since") LocalDateTime since, @Param("excludeIdx") Long excludeIdx);

    // 본사 대시보드용 - 발주 유형별 최근 건수 (자동/수동 KPI)
    long countByOrdersTypeAndCreatedAtAfter(OrdersType ordersType, LocalDateTime since);

    // 본사 대시보드용 - 상태별 발주 건수 (확정 대기 KPI)
    long countByOrdersStatus(OrdersStatus ordersStatus);

    // 본사 대시보드용 - 이상 발주 건수 KPI
    long countByIsDangerTrue();

    // 본사 대시보드용 - 이상 발주 월별 추이 차트 (상태별 그룹핑)
    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m'), o.ordersStatus, COUNT(o) " +
            "FROM Orders o WHERE o.isDanger = true AND o.createdAt >= :since " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m'), o.ordersStatus " +
            "ORDER BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m')")
    List<Object[]> findDangerStatsByMonth(@Param("since") LocalDateTime since);

    // 본사 대시보드용 - 주간 발주 추이 차트 (일별 건수)
    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%m-%d'), COUNT(o) " +
            "FROM Orders o WHERE o.createdAt >= :since " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%m-%d') " +
            "ORDER BY FUNCTION('DATE_FORMAT', o.createdAt, '%m-%d')")
    List<Object[]> findWeeklyOrderStats(@Param("since") LocalDateTime since);

    // 점주 대시보드용 - 미확정 제안 발주서 개수 KPI
    long countByStore_IdxAndOrdersStatusAndOrdersType(Long storeIdx, OrdersStatus ordersStatus, OrdersType ordersType);

    // 점주 대시보드용 - 미확정 제안 발주서 목록
    List<Orders> findAllByStore_IdxAndOrdersStatusAndOrdersTypeOrderByCreatedAtDesc(Long storeIdx, OrdersStatus ordersStatus, OrdersType ordersType);

    // 점주 대시보드용 - 기간별 승인 발주 금액 합계 (매출 KPI)
    @Query("SELECT COALESCE(SUM(o.price), 0) FROM Orders o WHERE o.store.idx = :storeIdx AND o.ordersStatus = 'APPROVE' AND o.createdAt >= :from AND o.createdAt < :to")
    long sumApprovedPriceByStoreAndPeriod(@Param("storeIdx") Long storeIdx, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    List<Orders> findAllByStoreIdx(Long storeIdx);
}
