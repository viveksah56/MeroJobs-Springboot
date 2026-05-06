package com.backend.Services.Impl;

import com.backend.Event.NotificationEvent;
import com.backend.Services.FirebaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FirebaseServiceImpl implements FirebaseService {

    @Override
    public void sendFirebaseNotification(NotificationEvent event) {
        log.warn("Firebase notification not yet implemented — recipient: '{}', title: '{}'",
                event.getRecipientId(), event.getTitle());
    }
}