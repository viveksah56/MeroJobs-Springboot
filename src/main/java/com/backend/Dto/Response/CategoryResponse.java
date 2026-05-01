package com.backend.Dto.Response;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(

        UUID categoryId,
        String name,
        String description,
        boolean active,

        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy

) {
}