package com.backend.Dto.Response;

import java.time.Instant;
import java.util.UUID;

public record ActiveSessionsResponse(
        UUID userDeviceId,
        String deviceId,
        String deviceType,
        String os,
        String browser,
        String ipAddress,
        boolean trusted,
        Instant firstSeenAt,
        Instant lastSeenAt,
        boolean currentDevice
) {}