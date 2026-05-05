package com.backend.Controller;

import com.backend.Dto.Request.JobApplicationRequest;
import com.backend.Dto.Request.JobRequest;
import com.backend.Dto.Request.JobSearchRequest;
import com.backend.Dto.Response.ApiResponse;
import com.backend.Dto.Response.JobApplicationResponse;
import com.backend.Dto.Response.JobResponse;
import com.backend.Dto.Response.PaginationResponse;
import com.backend.Enum.ExperienceLevel;
import com.backend.Enum.JobStatus;
import com.backend.Enum.JobType;
import com.backend.Enum.WorkLocationType;
import com.backend.Services.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(jobService.createJob(request), "Job created successfully", HttpStatus.CREATED));
    }

    @PutMapping("/{jobId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @PathVariable UUID jobId,
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(ApiResponse.success(jobService.updateJob(jobId, request), "Job updated successfully", HttpStatus.OK));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success(jobService.getJobById(jobId)));
    }

    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> deleteJobById(@PathVariable UUID jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.ok(ApiResponse.success(null, "Job deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginationResponse<JobResponse>>> searchJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) WorkLocationType workLocationType,
            @RequestParam(required = false) ExperienceLevel experienceLevel,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        JobSearchRequest request = new JobSearchRequest(
                search, jobType, workLocationType, experienceLevel, categoryId, page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(ApiResponse.success(jobService.searchActiveJobs(request)));
    }

    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<PaginationResponse<JobResponse>>> getMyJobs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) JobStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                jobService.getMyJobs(page, size, sortBy, sortDir, search, status)));
    }

    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('JOBSEEKER')")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> applyToJob(
            @Valid @ModelAttribute JobApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        jobService.applyToJob(request),
                        "Application submitted successfully",
                        HttpStatus.CREATED));
    }

    @GetMapping("/admin/all-jobs")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<PaginationResponse<JobResponse>>> getAllJobsAdmin(
            @RequestParam(defaultValue = "1")          int page,
            @RequestParam(defaultValue = "10")         int size,
            @RequestParam(defaultValue = "createdAt")  String sortBy,
            @RequestParam(defaultValue = "desc")       String sortDir,
            @RequestParam(required = false)            String search,
            @RequestParam(required = false)            JobStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                jobService.getAllJobsAdmin(page, size, sortBy, sortDir, search, status)));
    }
}
