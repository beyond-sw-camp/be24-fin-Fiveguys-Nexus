package com.example.nexus.domain.notification;

import com.example.nexus.common.enums.NotificationType;
import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.notification.model.HeadNotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class HeadNotificationController {

    private final HeadNotificationService headNotificationService;
    private final SseEmitterService sseEmitterService;

    /**
     * 알림 목록 조회
     *
     * @param type 알림 타입 필터 (선택)
     * @param page 페이지 번호
     * @param size 페이지 크기
     */
    @GetMapping("/list")
    public ResponseEntity<BaseResponse> getNotifications(
            @RequestParam(required = false) NotificationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Slice<HeadNotificationDto.ListRes> result;
        if (type != null) {
            result = headNotificationService.getNotificationsByType(type, page, size);
        } else {
            result = headNotificationService.getNotifications(page, size);
        }
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 미읽음 알림 개수 조회
     */
    @GetMapping("/unread/count")
    public ResponseEntity<BaseResponse> getUnreadCount() {
        HeadNotificationDto.UnreadCountRes result = headNotificationService.getUnreadCount();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 단건 읽음 처리
     *
     * @param notificationIdx 알림 ID
     */
    @PutMapping("/{notificationIdx}/read")
    public ResponseEntity<BaseResponse> markAsRead(@PathVariable Long notificationIdx) {
        headNotificationService.markAsRead(notificationIdx);
        return ResponseEntity.ok(BaseResponse.success("success"));
    }

    /**
     * 전체 읽음 처리
     */
    @PutMapping("/read/all")
    public ResponseEntity<BaseResponse> markAllAsRead() {
        headNotificationService.markAllAsRead();
        return ResponseEntity.ok(BaseResponse.success("success"));
    }

    /**
     * SSE 구독 (실시간 알림 수신)
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        return sseEmitterService.subscribe();
    }
}
