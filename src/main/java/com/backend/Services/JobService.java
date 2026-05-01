package com.backend.Services;

import com.backend.Dto.Request.JobApplicationRequest;
import com.backend.Dto.Request.JobRequest;
import com.backend.Dto.Request.JobSearchRequest;
import com.backend.Dto.Response.JobApplicationResponse;
import com.backend.Dto.Response.JobResponse;
import com.backend.Dto.Response.PaginationResponse;
import com.backend.Enum.JobStatus;

import java.util.UUID;

public interface JobService {

    JobResponse createJob(JobRequest request);

    JobResponse updateJob(UUID jobId, JobRequest request);

    JobResponse getJobById(UUID jobId);

    void deleteJob(UUID jobId);

    PaginationResponse<JobResponse> searchActiveJobs(JobSearchRequest request);

    PaginationResponse<JobResponse> getMyJobs(int page, int size, String sortBy, String sortDir, String search, JobStatus status);

    JobApplicationResponse applyToJob(JobApplicationRequest request);

    PaginationResponse<JobResponse> getAllJobsAdmin(int page, int size, String sortBy, String sortDir, String search, JobStatus status);


}