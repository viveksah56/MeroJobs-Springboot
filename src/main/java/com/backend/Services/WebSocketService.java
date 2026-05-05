package com.backend.Services;

import com.backend.Event.NotificationEvent;

public interface WebSocketService {
    void sendWebSocketNotification(NotificationEvent event);
}