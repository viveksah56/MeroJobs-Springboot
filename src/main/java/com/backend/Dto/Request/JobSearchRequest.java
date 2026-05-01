package com.backend.Dto.Request;

import com.backend.Enum.ExperienceLevel;
import com.backend.Enum.JobType;
import com.backend.Enum.WorkLocationType;

import java.util.UUID;

public record JobSearchRequest(
        String search,
        JobType jobType,
        WorkLocationType workLocationType,
        ExperienceLevel experienceLevel,
        UUID categoryId,
        int page,
        int size,
        String sortBy,
        String sortDir
) {}