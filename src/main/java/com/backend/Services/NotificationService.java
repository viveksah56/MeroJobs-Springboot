package com.backend.Services;

import com.backend.Dto.Response.NotificationResponse;
import com.backend.Dto.Response.PaginationResponse;
import com.backend.Entity.Notification;
import com.backend.Enum.NotificationChannel;
import com.backend.Event.NotificationEvent;

import java.util.UUID;

public interface NotificationService {

    void send(NotificationEvent event);

    Notification save(NotificationEvent event, NotificationChannel channel);

    PaginationResponse<NotificationResponse> getAll(UUID userId, int page, int size);

    PaginationResponse<NotificationResponse> getUnread(UUID userId, int page, int size);

    long countUnread(UUID userId);

    void markAsRead(UUID notificationId);

    void markAllAsRead(UUID userId);
}