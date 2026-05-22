package com.example.statistics.kafka;

import com.example.statistics.event.KafkaTopics;
import com.example.statistics.event.PaymentEvent;
import com.example.statistics.event.PaymentEventItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final StringRedisTemplate redisTemplate;

    @KafkaListener(topics = KafkaTopics.POS_PAYMENT_CREATED, concurrency = "3")
    public void onPaymentCreated(PaymentEvent event) {
        log.info("[pos.payment.created] received: posPayIdx={}, storeIdx={}, payAmount={}, items={}",
                event.posPayIdx(), event.storeIdx(), event.payAmount(), event.items().size());

        // 멱등성 : Redis SETNX 기반 (기존 existedById DB 조회 -> Redis O(1) 체크)
        String processdKey = "processed:" + event.posPayIdx();
        Boolean isFirst = redisTemplate.opsForValue().setIfAbsent(processdKey, "1", Duration.ofDays(7));

        if(Boolean.FALSE.equals(isFirst)) {
            log.warn("[pos.payment.created] duplicate payment skipped: posPayIdx={}", event.posPayIdx());
            return;
        }

        // Redis 사전 집계 (DB INSERT 다음 호출)
        aggregateToRedis(event);
    }

    /**
     * 결제 이벤트로부터 6개 통계 키에 사전 집계.
     *
     * - sales:today:DATE          (String, INCRBY)
     * - sales:store:ranking:DATE  (Sorted Set, ZINCRBY)
     * - sales:menu:ranking:DATE   (Sorted Set, ZINCRBY)
     * - sales:hourly:DATE         (Hash, HINCRBY)
     * - sales:category:DATE       (Hash, HINCRBY)
     * - sales:payment:DATE        (Hash, HINCRBY)
     *
     * 각 키에 7일 TTL 설정 (Redis 메모리 관리).
     */
    private void aggregateToRedis(PaymentEvent event) {
        String date = event.paidAt().toLocalDate().toString();   // "2026-05-21"
        int hour = event.paidAt().getHour();
        Duration ttl = Duration.ofDays(7);

        // 1. 오늘 총 매출
        String todayKey = "sales:today:" + date;
        redisTemplate.opsForValue().increment(todayKey, event.payAmount());
        redisTemplate.expire(todayKey, ttl);

        // 2. 매장 매출 랭킹
        String storeRankKey = "sales:store:ranking:" + date;
        redisTemplate.opsForZSet().incrementScore(
                storeRankKey,
                event.storeIdx() + "|" + event.storeName(),
                event.payAmount()
        );
        redisTemplate.expire(storeRankKey, ttl);

        // 3. 시간대별 매출
        String hourlyKey = "sales:hourly:" + date;
        redisTemplate.opsForHash().increment(hourlyKey, String.valueOf(hour), event.payAmount());
        redisTemplate.expire(hourlyKey, ttl);

        // 4. 결제수단별 매출
        String paymentKey = "sales:payment:" + date;
        redisTemplate.opsForHash().increment(paymentKey, event.method(), event.payAmount());
        redisTemplate.expire(paymentKey, ttl);

        // 5, 6. 메뉴 판매 랭킹 + 카테고리별 매출 (items 순회)
        String menuRankKey = "sales:menu:ranking:" + date;
        String categoryKey = "sales:category:" + date;
        for (PaymentEventItem item : event.items()) {
            redisTemplate.opsForZSet().incrementScore(
                    menuRankKey,
                    item.menuIdx() + "|" + item.menuName(),
                    item.quantity()
            );
            redisTemplate.opsForHash().increment(categoryKey, item.menuCategoryName(), item.lineAmount());
        }
        redisTemplate.expire(menuRankKey, ttl);
        redisTemplate.expire(categoryKey, ttl);
    }
}