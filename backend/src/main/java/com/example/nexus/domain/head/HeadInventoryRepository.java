package com.example.nexus.domain.head;

import com.example.nexus.common.enums.InventoryStatus;
import com.example.nexus.domain.head.model.HeadInventory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HeadInventoryRepository extends JpaRepository<HeadInventory, Long> {
    // 본사 재고 조회 & N+1 방지
    @Query("SELECT h FROM HeadInventory h JOIN FETCH h.product")
    List<HeadInventory> findAllWithProduct();

    Optional<HeadInventory> findByProductIdx(Long productIdx);

    // 대시보드용: 위험 재고 건수 조회
    long countByStatus(InventoryStatus status);

    // 대시보드용: 위험 재고 목록 조회 (Slice 페이징)
    Slice<HeadInventory> findByStatusIn(List<InventoryStatus> statuses, Pageable pageable);

    // 알림용: 재고가 있는 전체 목록 조회
    List<HeadInventory> findAllByCountGreaterThan(int count);
}
