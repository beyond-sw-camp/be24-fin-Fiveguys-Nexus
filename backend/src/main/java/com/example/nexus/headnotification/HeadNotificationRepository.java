package com.example.nexus.headnotification;

import com.example.nexus.headnotification.model.HeadNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeadNotificationRepository extends JpaRepository<HeadNotification, Long> {
}
