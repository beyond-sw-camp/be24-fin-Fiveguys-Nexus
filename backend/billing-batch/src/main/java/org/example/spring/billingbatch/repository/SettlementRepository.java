package org.example.spring.billingbatch.repository;

import org.example.spring.billingbatch.model.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
}
