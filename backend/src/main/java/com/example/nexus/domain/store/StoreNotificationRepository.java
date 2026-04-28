package com.example.nexus.domain.store;

import com.example.nexus.domain.store.model.StoreNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreNotificationRepository extends JpaRepository<StoreNotification, Long> {
}
