package com.backend.Dto.Response;

import com.backend.Enum.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record JobResponse(
        UUID jobId,
        String title,
        String description,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        JobType jobType,
        WorkLocationType workLocationType,
        ExperienceLevel experienceLevel,
        EducationLevel educationLevel,
        int experienceYears,
        Instant applicationDeadline,
        JobStatus status,
        String location,
        String rejectionReason,
        String categoryName,
        Set<String> skills,
        String postedBy,
        long totalApplications,
        Instant createdAt
) {}