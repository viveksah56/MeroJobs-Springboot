package com.backend.Dto.Response;

import com.backend.Enum.DocumentType;

import java.time.Instant;
import java.util.UUID;

public record DocumentResponse(
        UUID documentId,
        UUID jobSeekerId,
        DocumentType documentType,
        String fileUrl,
        String publicId,
        String fileName,
        boolean isDefault,
        Instant createdAt,
        Instant updatedAt
) {}