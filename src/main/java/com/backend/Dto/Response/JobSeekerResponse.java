package com.backend.Dto.Response;

import com.backend.Enum.AccountStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record JobSeekerResponse(

        UUID userId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String profilePictureUrl,
        String bio,
        AccountStatus status,
        String currentLocation,
        String educationLevel,
        String experienceLevel,
        String preferredJobType,
        List<SkillResponse.Summary> skills,
        Instant createdAt,
        Instant updatedAt

) {}