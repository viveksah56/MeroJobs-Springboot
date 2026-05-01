package com.backend.Dto.Request;

import jakarta.validation.constraints.NotNull;

public record JobApprovalRequest(

        @NotNull(message = "Approval decision is required")
        boolean approved,

        String reason
) {}