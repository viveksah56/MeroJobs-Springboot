package com.backend.Dto.Response;

public record DashboardStatsResponse(
        long totalUsers,
        long totalEmployees,
        long totalJobSeekers,
        long totalJobs,
        long activeJobs,
        long pendingApprovalJobs,
        long totalApplications,
        long pendingApplications,
        long hiredApplications
) {}