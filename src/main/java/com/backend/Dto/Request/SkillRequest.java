package com.backend.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SkillRequest(

        @NotBlank(message = "Skill name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @NotNull(message = "Category ID is required")
        UUID categoryId,

        Boolean active

) {
    public static SkillRequest of(String name, UUID categoryId) {
        return new SkillRequest(name, categoryId, true);
    }
}