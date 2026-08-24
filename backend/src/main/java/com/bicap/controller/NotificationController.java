package com.bicap.controller;

import com.bicap.dto.response.notification.NotificationResponse;
import com.bicap.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Danh sách thông báo của tài khoản hiện tại
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {

        return ResponseEntity.ok(
                notificationService.getMyNotifications()
        );
    }

    /**
     * Xem chi tiết thông báo
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotification(
            @PathVariable Long notificationId
    ) {

        return ResponseEntity.ok(
                notificationService.getNotification(notificationId)
        );
    }

    /**
     * Đánh dấu đã đọc
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId
    ) {

        return ResponseEntity.ok(
                notificationService.markAsRead(notificationId)
        );
    }

    /**
     * Đánh dấu tất cả đã đọc
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {

        notificationService.markAllAsRead();

        return ResponseEntity.noContent().build();
    }

}