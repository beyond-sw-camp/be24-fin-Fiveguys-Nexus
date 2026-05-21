package com.example.statistics.kafka;

import com.example.statistics.domain.store.StoreRepository;
import com.example.statistics.domain.store.model.Store;
import com.example.statistics.event.KafkaTopics;
import com.example.statistics.event.StoreEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreEventConsumer {

    private final StoreRepository storeRepository;

    @KafkaListener(topics = KafkaTopics.STORE_CREATED, concurrency = "3")
    public void onStoreCreated(StoreEvent event) {
        log.info("[store.created] received: idx = {}, name = {}", event.storeIdx(), event.storeName());
        upsert(event);
    }

    @KafkaListener(topics = KafkaTopics.STORE_UPDATED, concurrency = "3")
    public void onStoreUpdated(StoreEvent event) {
        log.info("[store.updated] received: idx={}, name={}, isDeleted={}",
                event.storeIdx(), event.storeName(), event.isDeleted());
        upsert(event);
    }

    private void upsert(StoreEvent event) {
        Store store = Store.builder()
                .idx(event.storeIdx())
                .storeName(event.storeName())
                .isDeleted(event.isDeleted())
                .build();
        storeRepository.save(store);
    }
}
