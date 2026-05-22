package com.example.batch.domain.head;

import com.example.batch.domain.head.model.HeadInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HeadInventoryRepository extends JpaRepository<HeadInventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM HeadInventory h WHERE h.product.idx = :productIdx")
    Optional<HeadInventory> findByProductIdxForUpdate(@Param("productIdx") Long productIdx);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM HeadInventory h WHERE h.product.idx IN :productIds ORDER BY h.product.idx ASC")
    List<HeadInventory> findAllByProductIdxInForUpdate(@Param("productIds") List<Long> productIds);

    /**
     * 재고 복구 시 사용하는 원자적 UPDATE.
     * <p>
     * SELECT FOR UPDATE + Java-side modify 패턴 대신 이 메서드를 사용하면
     * MariaDB InnoDB 커서 스캔 중 concurrent modification으로 발생하는
     * Error 1020 (ER_CHECKREAD) 을 방지할 수 있다.
     * count = count + delta 는 단일 행 UPDATE로 원자적으로 실행된다.
     */
    @Modifying
    @Query(value = """
            UPDATE head_inventory h
            JOIN product p ON h.product_idx = p.product_idx
            SET h.count  = h.count + :delta,
                h.status = CASE
                    WHEN (h.count + :delta) <= 0             THEN 'CRITICAL'
                    WHEN (h.count + :delta) <= p.min_stock / 2 THEN 'CRITICAL'
                    WHEN (h.count + :delta) <= p.min_stock   THEN 'LOW'
                    ELSE 'NORMAL'
                END
            WHERE h.product_idx = :productIdx
            """, nativeQuery = true)
    void restoreCountByProductIdx(@Param("productIdx") Long productIdx,
                                  @Param("delta") int delta);
}
