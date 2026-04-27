package com.example.nexus.pospay;

import com.example.nexus.pospay.model.PosPay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosPayRepository extends JpaRepository<PosPay, Long> {
}
