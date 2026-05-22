package com.example.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RejectInsufficientOrdersTasklet implements Tasklet {

    private final JdbcTemplate jdbcTemplate;
    private final RejectInsufficientOrdersService rejectService;

    /**
     * Step1 완료 후 processed=false가 남아있는 CONFIRMED 주문을 조회한다.
     * Step1이 모든 product 파티션을 완전히 커밋한 뒤에 실행되므로,
     * 이 시점에 processed=false가 남아있는 건 재고 부족으로 처리 불가한 item이다.
     */
    private static final String INSUFFICIENT_ORDER_IDS_SQL = """
            SELECT DISTINCT o.orders_idx
            FROM orders o
            JOIN orders_item oi ON o.orders_idx = oi.orders_idx
            WHERE o.order_status = 'CONFIRMED'
              AND oi.processed = false
            ORDER BY o.orders_idx ASC
            """;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        List<Long> orderIds = jdbcTemplate.queryForList(INSUFFICIENT_ORDER_IDS_SQL, Long.class);
        log.info("재고 부족 rollback 대상 order 수: {}", orderIds.size());

        for (Long orderId : orderIds) {
            try {
                rejectService.reject(orderId);
            } catch (Exception e) {
                log.error("orderId={} rollback 실패, 다음 건으로 계속", orderId, e);
            }
        }

        return RepeatStatus.FINISHED;
    }
}
