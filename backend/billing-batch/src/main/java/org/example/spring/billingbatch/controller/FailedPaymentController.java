package org.example.spring.billingbatch.controller;

import lombok.RequiredArgsConstructor;
import org.example.spring.billingbatch.model.AfterBilling;
import org.example.spring.billingbatch.model.FailedPayment;
import org.example.spring.billingbatch.repository.AfterBillingRepository;
import org.example.spring.billingbatch.repository.FailedPaymentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/billing/failed")
@RequiredArgsConstructor
public class FailedPaymentController {

    private final FailedPaymentRepository failedPaymentRepository;
    private final AfterBillingRepository afterBillingRepository;

    @GetMapping
    public ResponseEntity<List<FailedPayment>> getFailedPayments(@RequestParam String month) {
        List<FailedPayment> failedPayments = failedPaymentRepository.findByPayedMonth(month);
        return ResponseEntity.ok(failedPayments);
    }

    @GetMapping("/final")
    public ResponseEntity<List<AfterBilling>> getFinalFailedPayments(@RequestParam String month) {
        List<AfterBilling> finalFailures = afterBillingRepository.findByPayedMonthAndIsRetryFailedTrue(month);
        return ResponseEntity.ok(finalFailures);
    }
}
