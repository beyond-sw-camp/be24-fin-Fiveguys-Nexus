package com.example.statistics.domain.daily;

import com.example.statistics.domain.daily.model.DailyStoreSales;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * 일별 매장별 매출 영구 저장소.
 * Redis 키 `sales:store:ranking:DATE` (ZSet) 의 매장별 집계를 MariaDB 에 dump 한 결과를 관리.
 * 한 날짜에 매장 수만큼 row 가 존재 (aggregate_date + store_idx 복합 UNIQUE).
 */
public interface DailyStoreSalesRepository extends JpaRepository<DailyStoreSales, Long> {

    /**
     * 특정 날짜의 모든 매장 매출을 조회.
     * 매장 수만큼 row 가 반환되므로 List 형태.
     *
     * @param aggregateDate 조회할 집계 날짜
     * @return 해당 날짜의 매장별 매출 목록 (없으면 빈 List)
     */
    List<DailyStoreSales> findByAggregateDate(LocalDate aggregateDate);

    /**
     * 특정 날짜의 dump 가 이미 수행되었는지 확인.
     * 스케줄러/수동 트리거 의 멱등성 체크에 사용.
     *
     * @param aggregateDate 확인할 집계 날짜
     * @return 이미 dump 되었으면 true, 아니면 false
     */
    boolean existsByAggregateDate(LocalDate aggregateDate);
}
