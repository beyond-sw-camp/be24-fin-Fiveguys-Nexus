package com.example.statistics.event;

public record StoreEvent(
        Long storeIdx,
        String storeName,
        Boolean isDeleted
) {}