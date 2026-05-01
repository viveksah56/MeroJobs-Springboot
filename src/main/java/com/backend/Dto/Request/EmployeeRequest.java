package com.backend.Dto.Request;

import com.backend.Enum.AccountStatus;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

public record EmployeeRequest(

        @NotBlank(message = "First name is required")
        @Size(min = 2, message = "First name must be at least 2 characters long")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        String phone,

        MultipartFile profilePicture,

        String bio,

        @NotNull(message = "Account status is required")
        AccountStatus status,

        @NotBlank(message = "Company name is required")
        String companyName,

        @NotBlank(message = "Job title is required")
        String jobTitle,

        String department,

        String workLocation,

        @NotNull(message = "Salary is required")
        @Positive(message = "Salary must be greater than 0")
        Double salary
) {
}