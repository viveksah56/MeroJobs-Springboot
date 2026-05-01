package com.backend.Dto.Request;

import com.backend.Enum.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record JobRequest(

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Minimum salary is required")
        @Positive(message = "Minimum salary must be positive")
        BigDecimal salaryMin,

        @NotNull(message = "Maximum salary is required")
        @Positive(message = "Maximum salary must be positive")
        BigDecimal salaryMax,

        @NotNull(message = "Job type is required")
        JobType jobType,

        @NotNull(message = "Work location type is required")
        WorkLocationType workLocationType,

        @NotNull(message = "Experience level is required")
        ExperienceLevel experienceLevel,

        @NotNull(message = "Education level is required")
        EducationLevel educationLevel,

        @Min(value = 0, message = "Experience years must be 0 or more")
        int experienceYears,

        @NotNull(message = "Application deadline is required")
        @Future(message = "Deadline must be in the future")
        Instant applicationDeadline,

        String location,

        @NotNull(message = "Category is required")
        UUID categoryId,

        Set<UUID> skillIds,

        JobStatus status
) {}