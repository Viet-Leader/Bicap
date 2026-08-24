package com.bicap.dto.response.notification;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long notificationId;

    private String title;

    private String content;

    private Boolean isRead;

    private LocalDateTime createdAt;

}