package com.example.statistics.domain.daily;

import com.example.statistics.domain.daily.model.DailyTotalSales;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * 일별 총 매출 영구 저장소.
 * Redis 키 `sales:today:DATE` 의 일별 집계를 MariaDB 에 dump 한 결과를 관리.
 * 한 날짜당 1 row 만 존재 (aggregate_date UNIQUE).
 */
public interface DailyTotalSalesRepository extends JpaRepository<DailyTotalSales, Long> {

    /**
     * 특정 날짜의 총 매출을 조회.
     *
     * @param aggregateDate 조회할 집계 날짜
     * @return 해당 날짜의 총 매출 (없으면 Optional.empty())
     */
    Optional<DailyTotalSales> findByAggregateDate(LocalDate aggregateDate);

    /**
     * 특정 날짜의 dump 가 이미 수행되었는지 확인.
     * 스케줄러/수동 트리거 의 멱등성 체크에 사용.
     *
     * @param aggregateDate 확인할 집계 날짜
     * @return 이미 dump 되었으면 true, 아니면 false
     */
    boolean existsByAggregateDate(LocalDate aggregateDate);
}
