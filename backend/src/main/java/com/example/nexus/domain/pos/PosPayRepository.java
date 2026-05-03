package com.example.nexus.domain.pos;

import com.example.nexus.domain.pos.model.PosPay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PosPayRepository extends JpaRepository<PosPay, Long> {
    List<PosPay> findByStore_IdxAndPaidAtBetweenOrderByPaidAtDesc(Long storeIdx, LocalDateTime from, LocalDateTime to);
}
