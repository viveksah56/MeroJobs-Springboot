package com.backend.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        String description,

        Boolean active

) {
    public static CategoryRequest of(String name, String description) {
        return new CategoryRequest(name, description, true);
    }
}