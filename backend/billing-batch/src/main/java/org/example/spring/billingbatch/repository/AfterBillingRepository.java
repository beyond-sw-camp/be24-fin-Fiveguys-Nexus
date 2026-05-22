package org.example.spring.billingbatch.repository;

import org.example.spring.billingbatch.model.AfterBilling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AfterBillingRepository extends JpaRepository<AfterBilling, Long> {
    boolean existsByStoreIdxAndPayedMonth(Long storeIdx, String payedMonth);
    boolean existsByStoreIdxAndPayedMonthAndIsSuccessTrue(Long storeIdx, String payedMonth);
}
