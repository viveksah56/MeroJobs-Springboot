package com.backend.Services;

import com.backend.Event.NotificationEvent;

public interface EmailService {
    void sendEmail(NotificationEvent event);

}