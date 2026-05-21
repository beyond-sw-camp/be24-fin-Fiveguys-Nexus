package com.example.nexus.event;

public record StoreEvent(
        Long storeIdx,
        String storeName,
        Boolean isDeleted
) {}