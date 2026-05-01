package com.backend.Dto.Response;

import com.backend.Enum.ApplicationStatus;

import java.time.Instant;
import java.util.UUID;

public record JobApplicationResponse(
        UUID applicationId,
        UUID jobId,
        String jobTitle,
        String companyName,
        UUID jobSeekerId,
        String jobSeekerName,
        String jobSeekerEmail,
        ApplicationStatus status,
        String resumeUrl,
        String resumeFileName,
        String coverLetterUrl,
        String coverLetterFileName,
        String employeeNote,
        String adminNote,
        Instant reviewedAt,
        Instant decidedAt,
        Instant createdAt
) {}