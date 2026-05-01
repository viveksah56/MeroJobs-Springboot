package com.backend.Dto.Response;

import java.time.Instant;
import java.util.UUID;

public record UserDeviceResponse(
        UUID userDeviceId,
        String deviceId,
        String deviceType,
        String os,
        String browser,
        String ipAddress,
        boolean trusted,
        boolean active,
        Instant firstSeenAt,
        Instant lastSeenAt
) {}