package com.example.batch.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RestController
@RequestMapping("/batch/jobs")
@RequiredArgsConstructor
public class BatchJobController {

    private final JobLauncher jobLauncher;
    private final Job approveConfirmedOrdersJob;

    private final AtomicBoolean running = new AtomicBoolean(false);

    @PostMapping("/approve")
    public ResponseEntity<String> triggerApprove() {
        if (!running.compareAndSet(false, true)) {
            return ResponseEntity.status(409).body("batch job already running");
        }
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("runAt", LocalDateTime.now())
                    .toJobParameters();

            log.info("발주 일괄승인 배치 시작 runAt={}", LocalDateTime.now());
            jobLauncher.run(approveConfirmedOrdersJob, params);
            log.info("발주 일괄승인 배치 완료");

            return ResponseEntity.ok("batch job started");
        } catch (Exception e) {
            log.error("발주 일괄승인 배치 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("batch job failed: " + e.getMessage());
        } finally {
            running.set(false);
        }
    }
}
