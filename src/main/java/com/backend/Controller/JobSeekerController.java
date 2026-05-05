package com.backend.Controller;

import com.backend.Dto.Request.JobSeekerRequest;
import com.backend.Dto.Request.PaginationRequest;
import com.backend.Dto.Response.ApiResponse;
import com.backend.Dto.Response.JobSeekerResponse;
import com.backend.Dto.Response.PaginationResponse;
import com.backend.Services.JobSeekerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/job-seekers")
@Slf4j
@RequiredArgsConstructor
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;

    @PostMapping
    public ResponseEntity<ApiResponse<JobSeekerResponse>> createJobSeeker(
            @Valid @RequestBody JobSeekerRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(jobSeekerService.createJobSeeker(request),
                        "Job seeker created successfully"));
    }

    @PutMapping("/{jobSeekerId}")
    @PreAuthorize("hasRole('JOBSEEKER')")
    public ResponseEntity<ApiResponse<JobSeekerResponse>> updateJobSeeker(
            @PathVariable UUID jobSeekerId,
            @Valid @RequestBody JobSeekerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                jobSeekerService.updateJobSeeker(jobSeekerId, request),
                "Job seeker updated successfully"
        ));
    }

    @GetMapping("/{jobSeekerId}")
    @PreAuthorize("hasAnyRole('JOBSEEKER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<ApiResponse<JobSeekerResponse>> getJobSeekerById(
            @PathVariable UUID jobSeekerId) {
        return ResponseEntity.ok(ApiResponse.success(
                jobSeekerService.getJobSeekerById(jobSeekerId),
                "Job seeker retrieved successfully"
        ));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaginationResponse<JobSeekerResponse>>> getAllJobSeekers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return ResponseEntity.ok(ApiResponse.success(
                jobSeekerService.getAllJobSeekers(page, size, search, sort, sortDirection),
                "Job seekers retrieved successfully"
        ));
    }

    @GetMapping("/skills/{skillId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaginationResponse<JobSeekerResponse>>> getJobSeekersBySkillId(
            @PathVariable UUID skillId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return ResponseEntity.ok(ApiResponse.success(
                jobSeekerService.getJobSeekersBySkillId(skillId, page, size, search, sort, sortDirection),
                "Job seekers retrieved successfully"
        ));
    }

    @GetMapping("/{jobSeekerId}/applied-jobs")
    @PreAuthorize("hasRole('JOBSEEKER')")
    public ResponseEntity<ApiResponse<PaginationResponse<JobSeekerResponse>>> getAllAppliedJobs(
            @PathVariable UUID jobSeekerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return ResponseEntity.ok(ApiResponse.success(
                jobSeekerService.getAllAppliedJobs(new PaginationRequest(page, size, search, sort, sortDirection)),
                "Applied jobs retrieved successfully"
        ));
    }

    @GetMapping("/{jobSeekerId}/saved-jobs")
    @PreAuthorize("hasRole('JOBSEEKER')")
    public ResponseEntity<ApiResponse<PaginationResponse<JobSeekerResponse>>> getAllSavedJobs(
            @PathVariable UUID jobSeekerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return ResponseEntity.ok(ApiResponse.success(
                jobSeekerService.getAllSavedJobs(new PaginationRequest(page, size, search, sort, sortDirection)),
                "Saved jobs retrieved successfully"
        ));
    }

    @GetMapping("/{jobSeekerId}/bookmarked-jobs")
    @PreAuthorize("hasRole('JOBSEEKER')")
    public ResponseEntity<ApiResponse<PaginationResponse<JobSeekerResponse>>> getAllBookmarkedJobs(
            @PathVariable UUID jobSeekerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return ResponseEntity.ok(ApiResponse.success(
                jobSeekerService.getAllBookmarkedJobs(new PaginationRequest(page, size, search, sort, sortDirection)),
                "Bookmarked jobs retrieved successfully"
        ));
    }
}