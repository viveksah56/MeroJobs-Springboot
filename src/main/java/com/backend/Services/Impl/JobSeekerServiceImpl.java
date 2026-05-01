package com.backend.Services.Impl;

import com.backend.Dto.Request.JobSeekerRequest;
import com.backend.Dto.Response.JobSeekerResponse;
import com.backend.Dto.Response.PaginationResponse;
import com.backend.Entity.JobSeeker;
import com.backend.Entity.Skill;
import com.backend.Enum.AccountStatus;
import com.backend.Exception.ResourceNotFoundException;
import com.backend.Mapper.JobSeekerMapper;
import com.backend.Repository.JobSeekerRepository;
import com.backend.Repository.SkillRepository;
import com.backend.Services.FileService;
import com.backend.Services.JobSeekerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final JobSeekerMapper jobSeekerMapper;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;


    private String normalizeSearch(String search) {
        return search == null ? null : search.trim().toLowerCase();
    }

    @Override
    @Transactional
    public JobSeekerResponse createJobSeeker(JobSeekerRequest request) {
        jobSeekerRepository.findByEmail(request.email()).ifPresent(js -> {
            throw new IllegalArgumentException("Job seeker with email '" + request.email() + "' already exists");
        });
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
                .status(AccountStatus.ACTIVE)
                .build();
        JobSeeker saved = jobSeekerRepository.save(jobSeeker);
       return jobSeekerMapper.toJobSeekerResponse(saved);

    }

    @Override
    public JobSeekerResponse updateJobSeeker(UUID jobSeekerId, JobSeekerRequest request) {
        return null;
    }

    @Override
    public JobSeekerResponse getJobSeekerById(UUID jobSeekerId) {
        return null;
    }

    @Override
    public PaginationResponse<JobSeekerResponse> getAllJobSeekers(int page, int size, String search, String sort, String sortDirection) {
        return null;
    }

    @Override
    public PaginationResponse<JobSeekerResponse> getJobSeekersBySkillId(UUID skillId, int page, int size, String search, String sort, String sortDirection) {
        return null;
    }
}