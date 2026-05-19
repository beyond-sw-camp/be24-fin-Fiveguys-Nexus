package com.example.pos.domain.orders;

import com.example.pos.common.enums.OrdersStatus;
import com.example.pos.common.enums.OrdersType;
import com.example.pos.domain.orders.model.Orders;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {

    // Specification 기반 목록 조회 시 Store를 한 번에 JOIN FETCH (N+1 방지)
    @Override
    @EntityGraph(attributePaths = {"store"})
    Page<Orders> findAll(Specification<Orders> spec, Pageable pageable);

}
