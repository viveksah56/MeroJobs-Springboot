package com.backend.Dto.Response;

import java.time.Instant;
import java.util.UUID;

public record SkillResponse(

        UUID skillId,
        String name,
        boolean active,
        UUID categoryId,
        String categoryName,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy

) {
    public record Summary(
            UUID skillId,
            String name
    ) {}
}