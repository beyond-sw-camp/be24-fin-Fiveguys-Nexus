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
     * Step1 완료 후 스테이징 기준으로 processed=false 가 남아있는 CONFIRMED 주문을 조회한다.
     * <p>
     * orders_item_staging 에 없는 항목(배치 실행 중 새로 유입된 발주)은 조회 대상에서 제외되므로,
     * 신규 유입 발주를 재고 부족으로 오판하는 문제가 발생하지 않는다.
     */
    private static final String INSUFFICIENT_ORDER_IDS_SQL = """
            SELECT DISTINCT s.orders_idx
            FROM order_batch.orders_item_staging s
            JOIN orders_item oi ON s.orders_item_idx = oi.orders_item_idx
            WHERE oi.processed = false
            ORDER BY s.orders_idx ASC
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
