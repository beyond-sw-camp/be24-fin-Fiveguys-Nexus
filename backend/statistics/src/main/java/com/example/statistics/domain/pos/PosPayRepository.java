package com.example.statistics.domain.pos;

import com.example.statistics.domain.pos.model.PosPay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosPayRepository extends JpaRepository<PosPay, Long> {
}