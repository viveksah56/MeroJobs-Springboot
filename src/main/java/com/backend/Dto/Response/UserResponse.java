package com.backend.Dto.Response;

import com.backend.Enum.AccountStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String profilePictureUrl,
        String bio,
        AccountStatus status,
        Set<String> roles,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {}