package com.backend.Dto.Response;

import com.backend.Enum.NotificationChannel;
import com.backend.Enum.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(

        UUID notificationId,
        NotificationType type,
        NotificationChannel channel,
        String title,
        String message,
        UUID referenceId,
        String referenceType,
        boolean read,
        boolean sent,
        Instant createdAt

) {}