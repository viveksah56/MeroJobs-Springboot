package com.backend.Mapper;

import com.backend.Dto.Request.JobRequest;
import com.backend.Dto.Response.JobApplicationResponse;
import com.backend.Dto.Response.JobResponse;
import com.backend.Entity.Job;
import com.backend.Entity.JobApplication;
import com.backend.Entity.Skill;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class JobMapper {

    public JobResponse toResponse(Job job) {
        return new JobResponse(
                job.getJobId(),
                job.getTitle(),
                job.getDescription(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getJobType(),
                job.getWorkLocationType(),
                job.getExperienceLevel(),
                job.getEducationLevel(),
                job.getExperienceYears(),
                job.getApplicationDeadline(),
                job.getStatus(),
                job.getLocation(),
                job.getRejectionReason(),
                job.getCategory() != null ? job.getCategory().getName() : null,
                job.getSkills().stream().map(Skill::getName).collect(Collectors.toSet()),
                job.getPostedBy().getFirstName() + " " + job.getPostedBy().getLastName(),
                job.getApplications().size(),
                job.getCreatedAt()
        );
    }

    public JobApplicationResponse toApplicationResponse(JobApplication application) {
        return new JobApplicationResponse(
                application.getApplicationId(),
                application.getJob().getJobId(),
                application.getJob().getTitle(),
                application.getJob().getPostedBy().getCompanyName(),
                application.getJobSeeker().getUserId(),
                application.getJobSeeker().getFirstName() + " " + application.getJobSeeker().getLastName(),
                application.getJobSeeker().getEmail(),
                application.getStatus(),
                application.getResume() != null ? application.getResume().getFileUrl() : null,
                application.getResume() != null ? application.getResume().getFileName() : null,
                application.getCoverLetter() != null ? application.getCoverLetter().getFileUrl() : null,
                application.getCoverLetter() != null ? application.getCoverLetter().getFileName() : null,
                application.getEmployeeNote(),
                application.getAdminNote(),
                application.getReviewedAt(),
                application.getDecidedAt(),
                application.getCreatedAt()
        );
    }

    public void updateEntity(Job job, JobRequest request) {
        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setSalaryMin(request.salaryMin());
        job.setSalaryMax(request.salaryMax());
        job.setJobType(request.jobType());
        job.setStatus(request.status());
        job.setWorkLocationType(request.workLocationType());
        job.setExperienceLevel(request.experienceLevel());
        job.setEducationLevel(request.educationLevel());
        job.setExperienceYears(request.experienceYears());
        job.setApplicationDeadline(request.applicationDeadline());
        job.setLocation(request.location());
    }
}