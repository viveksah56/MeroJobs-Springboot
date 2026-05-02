package com.backend.Services;

import com.backend.Dto.Request.JobSeekerRequest;
import com.backend.Dto.Request.PaginationRequest;
import com.backend.Dto.Response.JobSeekerResponse;
import com.backend.Dto.Response.PaginationResponse;

import java.util.UUID;

public interface JobSeekerService {

    JobSeekerResponse createJobSeeker(JobSeekerRequest request);

    JobSeekerResponse updateJobSeeker(UUID jobSeekerId, JobSeekerRequest request);

    JobSeekerResponse getJobSeekerById(UUID jobSeekerId);

    PaginationResponse<JobSeekerResponse> getAllJobSeekers(int page, int size, String search, String sort, String sortDirection);

    PaginationResponse<JobSeekerResponse> getJobSeekersBySkillId(UUID skillId, int page, int size, String search, String sort, String sortDirection);

    PaginationResponse<JobSeekerResponse> getAllAppliedJobs(PaginationRequest paginationRequest);

    PaginationResponse<JobSeekerResponse> getAllSavedJobs(PaginationRequest paginationRequest);

    PaginationResponse<JobSeekerResponse> getAllBookmarkedJobs(PaginationRequest paginationRequest);
}