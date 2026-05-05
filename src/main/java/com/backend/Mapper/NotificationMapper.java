package com.backend.Mapper;

import com.backend.Dto.Response.NotificationResponse;
import com.backend.Entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getNotificationId(),
                notification.getType(),
                notification.getChannel(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getReferenceId(),
                notification.getReferenceType(),
                notification.isRead(),
                notification.isSent(),
                notification.getCreatedAt()
        );
    }
}