package com.example.nexus.storenotification;

import com.example.nexus.storenotification.model.StoreNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreNotificationRepository extends JpaRepository<StoreNotification, Long> {
}
