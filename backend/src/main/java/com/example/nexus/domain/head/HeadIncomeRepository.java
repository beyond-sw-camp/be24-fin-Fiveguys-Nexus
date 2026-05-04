package com.example.nexus.domain.head;

import com.example.nexus.domain.head.model.HeadIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface HeadIncomeRepository extends JpaRepository<HeadIncome, Long> {

    // 대시보드용
    @Query("SELECT COALESCE(SUM(h.price), 0) FROM HeadIncome h WHERE h.orders.createdAt >= :since")
    long sumPriceByOrdersCreatedAtAfter(@Param("since") LocalDateTime since);
}
