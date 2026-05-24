package org.example.spring.billingbatch.repository;

import org.example.spring.billingbatch.model.FailedPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedPaymentRepository extends JpaRepository<FailedPayment, Long> {
    List<FailedPayment> findByPayedMonth(String payedMonth);
    boolean existsByStoreIdxAndPayedMonth(Long storeIdx, String payedMonth);
}
