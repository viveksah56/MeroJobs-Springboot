package com.backend.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record JobSeekerRequest(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        String phone,

        String bio,

        @NotBlank(message = "Current location is required")
        String currentLocation,

        @NotBlank(message = "Education level is required")
        String educationLevel,

        @NotBlank(message = "Experience level is required")
        String experienceLevel,

        @NotBlank(message = "Preferred job type is required")
        String preferredJobType,

        @NotNull(message = "Skills are required")
        @NotEmpty(message = "At least one skill is required")
        @Size(max = 20, message = "Cannot add more than 20 skills")
        List<UUID> skillIds

) {}