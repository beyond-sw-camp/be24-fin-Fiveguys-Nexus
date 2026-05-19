package com.example.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class OrdersApproveJobConfig {

    private static final int CHUNK_SIZE = 100;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job approveConfirmedOrdersJob(Step approveConfirmedOrdersStep) {
        return new JobBuilder("approveConfirmedOrdersJob", jobRepository)
                .start(approveConfirmedOrdersStep)
                .build();
    }

    @Bean
    public Step approveConfirmedOrdersStep(ItemWriter<Long> orderApproveWriter) {
        return new StepBuilder("approveConfirmedOrdersStep", jobRepository)
                .<Long, Long>chunk(CHUNK_SIZE, transactionManager)
                .reader(confirmedOrderIdsReader())
                .writer(orderApproveWriter)
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<Long> confirmedOrderIdsReader() {
        return new JdbcCursorItemReaderBuilder<Long>()
                .name("confirmedOrderIdsReader")
                .dataSource(dataSource)
                .sql("SELECT orders_idx FROM orders WHERE order_status = 'CONFIRMED' ORDER BY orders_idx ASC")
                .rowMapper((rs, rowNum) -> rs.getLong("orders_idx"))
                .build();
    }
}
