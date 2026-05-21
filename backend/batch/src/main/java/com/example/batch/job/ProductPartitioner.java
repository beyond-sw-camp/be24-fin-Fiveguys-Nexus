package com.example.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductPartitioner implements Partitioner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        List<Long> productIds = jdbcTemplate.queryForList(
                "SELECT DISTINCT oi.product_idx " +
                "FROM orders_item oi " +
                "JOIN orders o ON oi.orders_idx = o.orders_idx " +
                "WHERE o.order_status = 'CONFIRMED' " +
                "  AND oi.processed = false",
                Long.class
        );

        Map<String, ExecutionContext> partitions = new LinkedHashMap<>();
        for (Long productId : productIds) {
            ExecutionContext ctx = new ExecutionContext();
            ctx.putLong("productId", productId);
            partitions.put("partition_product_" + productId, ctx);
        }
        return partitions;
    }
}
