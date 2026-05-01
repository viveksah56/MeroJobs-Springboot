package com.backend.Dto.Response;

import lombok.Builder;

@Builder
public record LoginResponse(
        String token
) {
}
