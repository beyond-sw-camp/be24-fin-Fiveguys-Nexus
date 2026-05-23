package org.example.spring.billingbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class MonthlyBillingScheduler {

    private final JobLauncher jobLauncher;
    private final Job settlementJob; // BillingBatch에 정의된 Job Bean 주입

    // 매달 1일 0시 0분 0초에 실행
    @Scheduled(cron = "0 0 4 10 * *")
    public void executeMonthlyBilling() {
        try {
            // 배치가 매번 새로운 Job 인스턴스로 인식하고 실행될 수 있도록 현재 시간을 파라미터로 주입합니다.
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("requestDate", LocalDateTime.now().toString())
                    .toJobParameters();

            System.out.println("--- 월 정산 배치 시작 ---");
            jobLauncher.run(settlementJob, jobParameters);
            System.out.println("--- 월 정산 배치 종료 ---");

        } catch (Exception e) {
            System.err.println("배치 실행 실패: " + e.getMessage());
        }
    }
}