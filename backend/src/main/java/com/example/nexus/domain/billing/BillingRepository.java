package com.example.nexus.domain.billing;

import com.example.nexus.domain.billing.model.Billing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingRepository extends JpaRepository<Billing, Long> {
}
