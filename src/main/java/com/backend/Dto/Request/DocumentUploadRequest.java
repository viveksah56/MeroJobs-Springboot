package com.backend.Dto.Request;

import com.backend.Enum.DocumentType;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record DocumentUploadRequest(

        @NotNull(message = "Document type is required")
        DocumentType documentType,

        @NotNull(message = "Job seeker ID is required")
        UUID jobSeekerId,

        @NotNull(message = "File is required")
        MultipartFile file,

        boolean isDefault

) {}