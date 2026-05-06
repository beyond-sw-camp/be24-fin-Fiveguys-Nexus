package com.example.nexus.domain.notification;

import com.example.nexus.common.enums.NotificationType;
import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.notification.model.StoreNotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/store/notification")
@RequiredArgsConstructor
public class StoreNotificationController {

    private final StoreNotificationService storeNotificationService;
    private final StoreSseEmitterService storeSseEmitterService;

    @GetMapping("/list")
    public ResponseEntity<BaseResponse> getNotifications(
            @RequestParam Long storeIdx,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Slice<StoreNotificationDto.ListRes> result;
        if (type != null) {
            result = storeNotificationService.getNotificationsByType(storeIdx, type, page, size);
        } else if (isRead != null) {
            result = storeNotificationService.getNotificationsByReadStatus(storeIdx, isRead, page, size);
        } else {
            result = storeNotificationService.getNotifications(storeIdx, page, size);
        }
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<BaseResponse> getUnreadCount(@RequestParam Long storeIdx) {
        StoreNotificationDto.UnreadCountRes result = storeNotificationService.getUnreadCount(storeIdx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PutMapping("/{notificationIdx}/read")
    public ResponseEntity<BaseResponse> markAsRead(@PathVariable Long notificationIdx) {
        storeNotificationService.markAsRead(notificationIdx);
        return ResponseEntity.ok(BaseResponse.success("success"));
    }

    @PutMapping("/read/all")
    public ResponseEntity<BaseResponse> markAllAsRead(@RequestParam Long storeIdx) {
        storeNotificationService.markAllAsRead(storeIdx);
        return ResponseEntity.ok(BaseResponse.success("success"));
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam Long storeIdx) {
        return storeSseEmitterService.subscribe(storeIdx);
    }
}
