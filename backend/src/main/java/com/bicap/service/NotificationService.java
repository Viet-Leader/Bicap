package com.bicap.service;

import com.bicap.dto.response.notification.NotificationResponse;
import com.bicap.entity.Account;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> getMyNotifications();

    NotificationResponse getNotification(Long notificationId);

    NotificationResponse markAsRead(Long notificationId);

    void markAllAsRead();

    void createNotification(
            Account account,
            String title,
            String content
    );
}