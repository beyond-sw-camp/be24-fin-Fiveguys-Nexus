package com.example.nexus.domain.pos;

import com.example.nexus.domain.pos.model.PosPay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosPayRepository extends JpaRepository<PosPay, Long> {
}
