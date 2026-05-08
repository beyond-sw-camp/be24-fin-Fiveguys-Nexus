package com.example.nexus.domain.pos;

import com.example.nexus.domain.pos.model.PosStoreInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PosStoreInventoryRepository extends JpaRepository<PosStoreInventory, Long> {
    // 목록 조회 시 Store·Product N+1 방지
    @Query("""
            SELECT p FROM PosStoreInventory p
            JOIN FETCH p.store
            JOIN FETCH p.product
            WHERE p.store.idx = :storeIdx
            """)
    List<PosStoreInventory> findByStoreIdxWithStoreAndProduct(@Param("storeIdx") Long storeIdx);

    List<PosStoreInventory> findByStore_IdxAndProduct_IdxOrderByManufacturedDateAsc(Long storeIdx, Long productIdx);
}
