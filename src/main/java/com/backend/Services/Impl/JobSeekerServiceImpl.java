package com.backend.Services.Impl;

import com.backend.Dto.Request.JobSeekerRequest;
import com.backend.Dto.Request.PaginationRequest;
import com.backend.Dto.Response.JobSeekerResponse;
import com.backend.Dto.Response.PaginationResponse;
import com.backend.Entity.JobSeeker;
import com.backend.Entity.Skill;
import com.backend.Exception.ResourceNotFoundException;
import com.backend.Mapper.JobSeekerMapper;
import com.backend.Repository.JobApplicationRepository;
import com.backend.Repository.JobSeekerRepository;
import com.backend.Repository.SavedJobRepository;
import com.backend.Repository.SkillRepository;
import com.backend.Services.JobSeekerService;
import com.backend.Util.AuthHelper;
import com.backend.Util.PageRequestBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSeekerServiceImpl implements JobSeekerService {

    private final JobSeekerRepository jobSeekerRepository;
    private final SkillRepository skillRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final SavedJobRepository savedJobRepository;
    private final JobSeekerMapper jobSeekerMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthHelper authHelper;

    @Override
    @Transactional
    public JobSeekerResponse createJobSeeker(JobSeekerRequest request) {
        if (jobSeekerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use: " + request.email());
        }

        List<Skill> skills = skillRepository.findAllById(request.skillIds());
        if (skills.size() != request.skillIds().size()) {
            throw new ResourceNotFoundException("One or more skills not found");
        }

        JobSeeker jobSeeker = JobSeeker.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .bio(request.bio())
                .currentLocation(request.currentLocation())
                .educationLevel(request.educationLevel())
                .experienceLevel(request.experienceLevel())
                .preferredJobType(request.preferredJobType())
                .skills(skills)
                .build();

        log.info("Creating job seeker with email: {}", request.email());
        return jobSeekerMapper.toJobSeekerResponse(jobSeekerRepository.save(jobSeeker));
    }

    @Override
    @Transactional
    public JobSeekerResponse updateJobSeeker(UUID jobSeekerId, JobSeekerRequest request) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found with id: " + jobSeekerId));

        jobSeekerMapper.updateEntity(jobSeeker, request);

        if (request.skillIds() != null && !request.skillIds().isEmpty()) {
            List<Skill> skills = skillRepository.findAllById(request.skillIds());
            if (skills.size() != request.skillIds().size()) {
                throw new ResourceNotFoundException("One or more skills not found");
            }
            jobSeeker.setSkills(skills);
        }

        log.info("Updating job seeker with id: {}", jobSeekerId);
        return jobSeekerMapper.toJobSeekerResponse(jobSeekerRepository.save(jobSeeker));
    }

    @Override
    @Transactional(readOnly = true)
    public JobSeekerResponse getJobSeekerById(UUID jobSeekerId) {
        return jobSeekerRepository.findById(jobSeekerId)
                .map(jobSeekerMapper::toJobSeekerResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found with id: " + jobSeekerId));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<JobSeekerResponse> getAllJobSeekers(int page, int size, String search, String sort, String sortDirection) {
        Pageable pageable = PageRequestBuilder.build(page, size, sort, sortDirection);

        return PaginationResponse.of(
                (search != null && !search.isBlank())
                        ? jobSeekerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        search, search, search, pageable)
                          .map(jobSeekerMapper::toJobSeekerResponse)
                        : jobSeekerRepository.findAll(pageable)
                          .map(jobSeekerMapper::toJobSeekerResponse)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<JobSeekerResponse> getJobSeekersBySkillId(UUID skillId, int page, int size, String search, String sort, String sortDirection) {
        if (!skillRepository.existsById(skillId)) {
            throw new ResourceNotFoundException("Skill not found with id: " + skillId);
        }

        Pageable pageable = PageRequestBuilder.build(page, size, sort, sortDirection);

        return PaginationResponse.of(
                (search != null && !search.isBlank())
                        ? jobSeekerRepository.findBySkills_SkillIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        skillId, search, search, pageable)
                          .map(jobSeekerMapper::toJobSeekerResponse)
                        : jobSeekerRepository.findBySkills_SkillId(skillId, pageable)
                          .map(jobSeekerMapper::toJobSeekerResponse)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<JobSeekerResponse> getAllAppliedJobs(PaginationRequest request) {
        UUID jobSeekerId = getCurrentJobSeekerId();
        Pageable pageable = PageRequestBuilder.build(request);

        return PaginationResponse.of(
                jobApplicationRepository.findByJobSeekerUserIdAndDeletedFalse(jobSeekerId, pageable)
                        .map(application -> jobSeekerMapper.toJobSeekerResponse(application.getJobSeeker()))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<JobSeekerResponse> getAllSavedJobs(PaginationRequest request) {
        UUID jobSeekerId = getCurrentJobSeekerId();
        Pageable pageable = PageRequestBuilder.build(request);

        return PaginationResponse.of(
                savedJobRepository.findByJobSeeker_UserId(jobSeekerId, pageable)
                        .map(savedJob -> jobSeekerMapper.toJobSeekerResponse(savedJob.getJobSeeker()))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<JobSeekerResponse> getAllBookmarkedJobs(PaginationRequest request) {
        UUID jobSeekerId = getCurrentJobSeekerId();
        Pageable pageable = PageRequestBuilder.build(request);

        return PaginationResponse.of(
                savedJobRepository.findByJobSeeker_UserIdAndBookmarkedTrue(jobSeekerId, pageable)
                        .map(savedJob -> jobSeekerMapper.toJobSeekerResponse(savedJob.getJobSeeker()))
        );
    }

    private UUID getCurrentJobSeekerId() {
        String email = authHelper.getCurrentEmail();
        return jobSeekerRepository.findByEmailAndDeletedFalse(email)
                .map(JobSeeker::getUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found with email: " + email));
    }
}