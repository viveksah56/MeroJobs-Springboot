package com.backend.Dto.Request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record DeviceInfo(

        String deviceId,
        String deviceType,
        String os,
        String browser,
        String userAgent,
        String ipAddress

) {
}