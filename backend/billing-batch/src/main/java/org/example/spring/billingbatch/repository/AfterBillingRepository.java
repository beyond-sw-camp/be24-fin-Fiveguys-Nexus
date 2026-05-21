package org.example.spring.billingbatch.repository;

import org.example.spring.billingbatch.model.AfterBilling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AfterBillingRepository extends JpaRepository<AfterBilling, Long> {
}
