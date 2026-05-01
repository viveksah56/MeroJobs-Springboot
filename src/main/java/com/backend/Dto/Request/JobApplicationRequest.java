package com.backend.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record JobApplicationRequest(

        @NotNull(message = "Job ID is required")
        UUID jobId,

        @NotBlank(message = "Cover letter is required")
        String coverLetter,

        MultipartFile resume
) {}
