package com.bicap.mapper;

import com.bicap.dto.response.notification.NotificationResponse;
import com.bicap.entity.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);

    List<NotificationResponse> toResponseList(
            List<Notification> notifications
    );

}