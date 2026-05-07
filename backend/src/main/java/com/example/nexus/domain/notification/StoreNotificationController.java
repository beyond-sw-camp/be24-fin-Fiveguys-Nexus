package com.example.nexus.domain.notification;

import com.example.nexus.common.enums.NotificationType;
import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.notification.model.StoreNotificationDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/store/notification")
@RequiredArgsConstructor
public class StoreNotificationController {

    private final StoreNotificationService storeNotificationService;

    /**
     * 가맹점 알림 목록 조회
     *
     * @param authUserDetails 인증된 사용자 정보
     * @param type 알림 타입 필터 (선택)
     * @param isRead 읽음 상태 필터 (선택)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return ResponseEntity 가맹점 알림 목록 (페이징)
     */
    @GetMapping("/list")
    public ResponseEntity<BaseResponse> getNotifications(
            @AuthenticationPrincipal AuthUserDetails authUserDetails,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Slice<StoreNotificationDto.ListRes> result;
        if (type != null) {
            result = storeNotificationService.getNotificationsByType(authUserDetails, type, page, size);
        } else if (isRead != null) {
            result = storeNotificationService.getNotificationsByReadStatus(authUserDetails, isRead, page, size);
        } else {
            result = storeNotificationService.getNotifications(authUserDetails, page, size);
        }
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 가맹점 미읽음 알림 개수 조회
     *
     * @param authUserDetails 인증된 사용자 정보
     * @return ResponseEntity 미읽음 알림 개수
     */
    @GetMapping("/unread/count")
    public ResponseEntity<BaseResponse> getUnreadCount(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        StoreNotificationDto.UnreadCountRes result = storeNotificationService.getUnreadCount(authUserDetails);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 가맹점 알림 단건 읽음 처리
     *
     * @param notificationIdx 읽음 처리할 알림의 고유 ID
     * @return ResponseEntity 처리 결과 메시지
     */
    @PutMapping("/{notificationIdx}/read")
    public ResponseEntity<BaseResponse> markAsRead(@PathVariable Long notificationIdx) {
        storeNotificationService.markAsRead(notificationIdx);
        return ResponseEntity.ok(BaseResponse.success("success"));
    }

    /**
     * 가맹점 알림 전체 읽음 처리
     *
     * @param authUserDetails 인증된 사용자 정보
     * @return ResponseEntity 처리 결과 메시지
     */
    @PutMapping("/read/all")
    public ResponseEntity<BaseResponse> markAllAsRead(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        storeNotificationService.markAllAsRead(authUserDetails);
        return ResponseEntity.ok(BaseResponse.success("success"));
    }

    /**
     * 가맹점 SSE 구독 (실시간 알림 수신)
     *
     * @param authUserDetails 인증된 사용자 정보
     * @return SseEmitter SSE 연결
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        return storeNotificationService.subscribe(authUserDetails);
    }
}
