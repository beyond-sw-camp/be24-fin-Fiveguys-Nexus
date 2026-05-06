package com.example.nexus.domain.notification;

import com.example.nexus.common.enums.NotificationType;
import com.example.nexus.domain.notification.model.HeadNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface HeadNotificationRepository extends JpaRepository<HeadNotification, Long> {

    Slice<HeadNotification> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Slice<HeadNotification> findAllByTypeOrderByCreatedAtDesc(NotificationType type, Pageable pageable);

    long countByIsReadFalse();

    @Modifying
    @Query("UPDATE HeadNotification n SET n.isRead = true WHERE n.isRead = false")
    void markAllAsRead();

    boolean existsByTypeAndTitleAndCreatedAtAfter(NotificationType type, String title, LocalDateTime after);
}
