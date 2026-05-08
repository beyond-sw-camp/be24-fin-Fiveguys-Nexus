package com.example.nexus.domain.pos;

import com.example.nexus.domain.pos.model.PosStoreInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

    // 결제 FIFO 차감 시 동시성 제어
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p FROM PosStoreInventory p
            WHERE p.store.idx = :storeIdx
              AND p.product.idx = :productIdx
            ORDER BY p.manufacturedDate ASC
            """)
    List<PosStoreInventory> findByStoreAndProductForUpdate(
            @Param("storeIdx") Long storeIdx,
            @Param("productIdx") Long productIdx
    );

}
