package com.example.nexus.domain.notification;

import com.example.nexus.common.enums.NotificationType;
import com.example.nexus.domain.notification.model.StoreNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface StoreNotificationRepository extends JpaRepository<StoreNotification, Long> {

    Slice<StoreNotification> findAllByStore_IdxOrderByCreatedAtDesc(Long storeIdx, Pageable pageable);

    Slice<StoreNotification> findAllByStore_IdxAndIsReadOrderByCreatedAtDesc(Long storeIdx, boolean isRead, Pageable pageable);

    Slice<StoreNotification> findAllByStore_IdxAndTypeOrderByCreatedAtDesc(Long storeIdx, NotificationType type, Pageable pageable);

    long countByStore_IdxAndIsReadFalse(Long storeIdx);

    @Modifying
    @Query("UPDATE StoreNotification n SET n.isRead = true WHERE n.store.idx = :storeIdx AND n.isRead = false")
    void markAllAsReadByStoreIdx(Long storeIdx);

    boolean existsByTypeAndTitleAndStore_IdxAndCreatedAtAfter(NotificationType type, String title, Long storeIdx, LocalDateTime after);

    @Modifying
    @Query("DELETE FROM StoreNotification n WHERE n.isRead = true AND n.createdAt < :before")
    void deleteReadBefore(LocalDateTime before);

    @Modifying
    @Query("DELETE FROM StoreNotification n WHERE n.isRead = false AND n.createdAt < :before")
    void deleteUnreadBefore(LocalDateTime before);
}
