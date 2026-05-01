package com.backend.Dto.Request;

import com.backend.Enum.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record ApplicationReviewRequest(

        @NotNull(message = "Status is required")
        ApplicationStatus status,

        String note
) {}