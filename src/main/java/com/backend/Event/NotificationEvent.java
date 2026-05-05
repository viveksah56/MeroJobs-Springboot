package com.backend.Event;

import com.backend.Enum.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class NotificationEvent {

    private final UUID recipientId;
    private final NotificationType type;
    private final String title;
    private final String message;
    private final UUID referenceId;
    private final String referenceType;
}