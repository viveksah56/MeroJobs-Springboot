package com.backend.Enum;

public enum NotificationChannel {
    IN_APP(1), FIREBASE(2), WEBSOCKET(0), EMAIL(3);

    NotificationChannel(int priority) {
    }


    public boolean isRealTime() {
        return this == WEBSOCKET || this == FIREBASE;
    }
}