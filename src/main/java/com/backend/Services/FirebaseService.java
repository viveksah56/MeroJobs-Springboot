package com.backend.Services;

import com.backend.Event.NotificationEvent;

public interface FirebaseService {
    void sendFirebaseNotification(NotificationEvent event);
}