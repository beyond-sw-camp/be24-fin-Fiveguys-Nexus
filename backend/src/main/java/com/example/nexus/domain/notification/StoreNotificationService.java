package com.example.nexus.domain.notification;

import com.example.nexus.common.enums.NotificationType;
import com.example.nexus.domain.notification.model.StoreNotification;
import com.example.nexus.domain.notification.model.StoreNotificationDto;
import com.example.nexus.domain.store.model.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreNotificationService {

    private final StoreNotificationRepository storeNotificationRepository;
    private final StoreSseEmitterService storeSseEmitterService;

    /**
     * 알림 목록 조회 (전체)
     */
    public Slice<StoreNotificationDto.ListRes> getNotifications(Long storeIdx, int page, int size) {
        return storeNotificationRepository.findAllByStore_IdxOrderByCreatedAtDesc(storeIdx, PageRequest.of(page, size))
                .map(StoreNotificationDto.ListRes::from);
    }

    /**
     * 알림 목록 조회 (읽음 상태별)
     */
    public Slice<StoreNotificationDto.ListRes> getNotificationsByReadStatus(Long storeIdx, boolean isRead, int page, int size) {
        return storeNotificationRepository.findAllByStore_IdxAndIsReadOrderByCreatedAtDesc(storeIdx, isRead, PageRequest.of(page, size))
                .map(StoreNotificationDto.ListRes::from);
    }

    /**
     * 알림 목록 조회 (타입별 필터)
     */
    public Slice<StoreNotificationDto.ListRes> getNotificationsByType(Long storeIdx, NotificationType type, int page, int size) {
        return storeNotificationRepository.findAllByStore_IdxAndTypeOrderByCreatedAtDesc(storeIdx, type, PageRequest.of(page, size))
                .map(StoreNotificationDto.ListRes::from);
    }

    /**
     * 미읽음 알림 개수 조회
     */
    public StoreNotificationDto.UnreadCountRes getUnreadCount(Long storeIdx) {
        long count = storeNotificationRepository.countByStore_IdxAndIsReadFalse(storeIdx);
        return StoreNotificationDto.UnreadCountRes.builder().count(count).build();
    }

    /**
     * 단건 읽음 처리
     */
    @Transactional
    public void markAsRead(Long notificationIdx) {
        StoreNotification notification = storeNotificationRepository.findById(notificationIdx)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        notification.markAsRead();
    }

    /**
     * 전체 읽음 처리
     */
    @Transactional
    public void markAllAsRead(Long storeIdx) {
        storeNotificationRepository.markAllAsReadByStoreIdx(storeIdx);
    }

    /**
     * 알림 생성 + SSE 실시간 전송
     */
    @Transactional
    public StoreNotification create(NotificationType type, String title, String content, Store store) {
        StoreNotification notification = StoreNotification.builder()
                .type(type)
                .title(title)
                .content(content)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .store(store)
                .build();
        StoreNotification saved = storeNotificationRepository.save(notification);
        storeSseEmitterService.send(store.getIdx(), StoreNotificationDto.ListRes.from(saved));
        return saved;
    }
}
