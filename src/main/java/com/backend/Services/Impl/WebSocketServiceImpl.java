package com.backend.Services.Impl;

import com.backend.Event.NotificationEvent;
import com.backend.Services.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketServiceImpl implements WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendWebSocketNotification(NotificationEvent event) {
        try {
            messagingTemplate.convertAndSendToUser(
                    event.getRecipientId().toString(),
                    "/queue/notifications",
                    event
            );
            log.info("WebSocket notification sent to user '{}'", event.getRecipientId());
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification to user '{}': {}",
                    event.getRecipientId(), e.getMessage(), e);
        }
    }
}