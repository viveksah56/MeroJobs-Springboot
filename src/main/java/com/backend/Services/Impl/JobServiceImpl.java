package com.backend.Services.Impl;

import com.backend.Dto.Request.JobApplicationRequest;
import com.backend.Dto.Request.JobRequest;
import com.backend.Dto.Request.JobSearchRequest;
import com.backend.Dto.Response.JobApplicationResponse;
import com.backend.Dto.Response.JobResponse;
import com.backend.Dto.Response.PaginationResponse;
import com.backend.Entity.*;
import com.backend.Enum.JobStatus;
import com.backend.Mapper.JobMapper;
import com.backend.Repository.*;
import com.backend.Services.FileService;
import com.backend.Services.JobService;
import com.backend.Util.PageRequestBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {


    private static final String RESUME_FOLDER = "job-applications/resumes";


    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final EmployeeRepository employeeRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final JobMapper jobMapper;
    private final FileService fileService;

    private String getCurrentEmail() {
        return Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
    }

    private Employee getCurrentEmployee() {
        return employeeRepository.findByEmailAndDeletedFalse(getCurrentEmail())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    private Set<Skill> resolveSkills(JobRequest request) {
        if (request.skillIds() == null || request.skillIds().isEmpty()) return new HashSet<>();
        return new HashSet<>(skillRepository.findAllBySkillIdIn(request.skillIds().stream().toList()));
    }

    private Category findCategory(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
    }

    private Job findActiveJob(UUID jobId) {
        return jobRepository.findByJobIdAndDeletedFalse(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found or is deleted: " + jobId));
    }

    private void assertOwnership(Job job, Employee employee) {
        if (!job.getPostedBy().getUserId().equals(employee.getUserId())) {
            throw new RuntimeException("Unauthorized: You do not own this job");
        }
    }

    private void assertEditable(Job job) {
        if (job.getStatus() == JobStatus.ACTIVE || job.getStatus() == JobStatus.CLOSED) {
            throw new RuntimeException("Cannot edit a job with status: " + job.getStatus());
        }
    }

    private String normalize(String search) {
        return (search == null || search.isBlank()) ? null : search.trim();
    }

    @Override
    public JobResponse createJob(JobRequest request) {
        Employee employee = getCurrentEmployee();
        Category category = findCategory(request.categoryId());
        Set<Skill> skills = resolveSkills(request);

        var status = request.status() != null ? request.status() : JobStatus.DRAFT;
        Job job = Job.builder()
                .title(request.title())
                .description(request.description())
                .salaryMin(request.salaryMin())
                .salaryMax(request.salaryMax())
                .jobType(request.jobType())
                .workLocationType(request.workLocationType())
                .experienceLevel(request.experienceLevel())
                .educationLevel(request.educationLevel())
                .experienceYears(request.experienceYears())
                .applicationDeadline(request.applicationDeadline())
                .location(request.location())
                .status(status)
                .postedBy(employee)
                .category(category)
                .skills(skills)
                .build();

        Job savedJob = jobRepository.save(job);
        log.info("Created job with ID: {}", savedJob.getJobId());
        return jobMapper.toResponse(savedJob);
    }

    @Override
    @Transactional
    public JobResponse updateJob(UUID jobId, JobRequest request) {
        Employee employee = getCurrentEmployee();
        Job job = findActiveJob(jobId);

        assertOwnership(job, employee);
        assertEditable(job);

        jobMapper.updateEntity(job, request);
        job.setCategory(findCategory(request.categoryId()));
        job.setSkills(resolveSkills(request));

        log.info("Updated job with ID: {}", job.getJobId());
        return jobMapper.toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobById(UUID jobId) {
        return jobMapper.toResponse(findActiveJob(jobId));
    }

    @Override
    public void deleteJob(UUID jobId) {
        Employee employee = getCurrentEmployee();
        Job job = findActiveJob(jobId);

        assertOwnership(job, employee);

        job.setDeleted(true);
        job.setDeletedAt(Instant.now());
        job.setDeletedBy(employee.getEmail());
        jobRepository.save(job);

        log.info("Job '{}' deleted by employee '{}'", jobId, employee.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<JobResponse> searchActiveJobs(JobSearchRequest request) {
        Pageable pageable = PageRequestBuilder.build(
                request.page(), request.size(), request.sortBy(), request.sortDir()
        );
        Page<Job> jobPage = jobRepository.searchActiveJobs(
                normalize(request.search()),
                JobStatus.ACTIVE,
                request.jobType(),
                request.workLocationType(),
                request.experienceLevel(),
                request.categoryId(),
                pageable
        );
        return buildJobPage(jobPage, request.page(), request.size(), request.sortBy(), request.sortDir());
    }

    @Override
    public PaginationResponse<JobResponse> getMyJobs(int page, int size, String sortBy, String sortDir,
                                                     String search, JobStatus status) {

        Employee employee = getCurrentEmployee();
        Pageable pageable = PageRequestBuilder.build(page, size, sortBy, sortDir);
        Page<Job> jobPage = jobRepository.findByEmployeeId(
                employee.getUserId(), status, normalize(search), pageable
        );
        return buildJobPage(jobPage, page, size, sortBy, sortDir);
    }

    @Override
    public JobApplicationResponse applyToJob(JobApplicationRequest request) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<JobResponse> getAllJobsAdmin(int page, int size, String sortBy, String sortDir,
                                                           String search, JobStatus status) {

        Pageable pageable = PageRequestBuilder.build(page, size, sortBy, sortDir);
        Page<Job> jobPage = jobRepository.searchAllJobs(normalize(search), status, pageable);
        return buildJobPage(jobPage, page, size, sortBy, sortDir);
    }

    private PaginationResponse<JobResponse> buildJobPage(Page<Job> page, int pageNum, int size,
                                                         String sortBy, String sortDir) {
        return new PaginationResponse<>(
                page.getContent().stream().map(jobMapper::toResponse).toList(),
                pageNum, size, page.getTotalElements(), page.getTotalPages(), sortBy, sortDir
        );
    }

    private JobSeeker getCurrentJobSeeker() {
        return jobSeekerRepository.findByEmailAndDeletedFalse(getCurrentEmail())
                .orElseThrow(() -> new RuntimeException("JobSeeker not found"));
    }
}
