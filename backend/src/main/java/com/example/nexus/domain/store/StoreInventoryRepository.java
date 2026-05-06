package com.example.nexus.domain.store;

import com.example.nexus.common.enums.InventoryStatus;
import com.example.nexus.domain.store.model.StoreInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StoreInventoryRepository extends JpaRepository<StoreInventory, Long> {
    List<StoreInventory> findByStoreIdx(Long storeIdx);
    Optional<StoreInventory> findByStoreIdxAndProductIdx(Long storeIdx, Long productIdx);

    // 점주 대시보드용 - 재고 위험 품목 KPI 카드 (상태별 개수)
    long countByStore_IdxAndStatus(Long storeIdx, InventoryStatus status);

    // 점주 대시보드용 - 재고 위험 품목 목록 (CRITICAL/LOW 상태 조회)
    List<StoreInventory> findByStore_IdxAndStatusInOrderByStatusDesc(Long storeIdx, List<InventoryStatus> statuses);

    Optional<StoreInventory> findByStore_IdxAndProduct_IdxAndManufacturedDate(Long storeIdx, Long productIdx, LocalDateTime manufacturedDate);

    List<StoreInventory> findByStore_IdxAndProduct_IdxOrderByManufacturedDateAsc(Long storeIdx, Long productIdx);
}
