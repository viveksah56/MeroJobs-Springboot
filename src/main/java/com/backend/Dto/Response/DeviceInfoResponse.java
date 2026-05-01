package com.backend.Dto.Response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public record DeviceInfoResponse(
        String deviceId,
        String deviceType,
        String os,
        String browser
) {

}
