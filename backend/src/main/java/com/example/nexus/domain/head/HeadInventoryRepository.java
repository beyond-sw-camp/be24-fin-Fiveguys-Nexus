package com.example.nexus.domain.head;

import com.example.nexus.common.enums.InventoryStatus;
import com.example.nexus.domain.head.model.HeadInventory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HeadInventoryRepository extends JpaRepository<HeadInventory, Long> {
    Optional<HeadInventory> findByProductIdx(Long productIdx);

    // 대시보드용: 위험 재고 건수 조회
    long countByStatus(InventoryStatus status);

    // 대시보드용: 위험 재고 목록 조회 (Slice 페이징)
    Slice<HeadInventory> findByStatusIn(List<InventoryStatus> statuses, Pageable pageable);
}
