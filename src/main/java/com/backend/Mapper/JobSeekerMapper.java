package com.backend.Mapper;

import com.backend.Dto.Request.JobSeekerRequest;
import com.backend.Dto.Response.DocumentResponse;
import com.backend.Dto.Response.JobSeekerResponse;
import com.backend.Dto.Response.SkillResponse;
import com.backend.Entity.Document;
import com.backend.Entity.JobSeeker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobSeekerMapper {

    public JobSeekerResponse toJobSeekerResponse(JobSeeker jobSeeker) {
        return new JobSeekerResponse(
                jobSeeker.getUserId(),
                jobSeeker.getFirstName(),
                jobSeeker.getLastName(),
                jobSeeker.getEmail(),
                jobSeeker.getPhone(),
                jobSeeker.getProfilePictureUrl(),
                jobSeeker.getBio(),
                jobSeeker.getStatus(),
                jobSeeker.getCurrentLocation(),
                jobSeeker.getEducationLevel(),
                jobSeeker.getExperienceLevel(),
                jobSeeker.getPreferredJobType(),
                jobSeeker.getSkills() != null
                        ? jobSeeker.getSkills().stream()
                          .map(skill -> new SkillResponse.Summary(
                                  skill.getSkillId(),
                                  skill.getName()
                          ))
                          .collect(Collectors.toList())
                        : List.of(),
                jobSeeker.getCreatedAt(),
                jobSeeker.getUpdatedAt()
        );
    }

    public DocumentResponse toDocumentResponse(Document document) {
        return new DocumentResponse(
                document.getDocumentId(),
                document.getJobSeeker().getUserId(),
                document.getDocumentType(),
                document.getFileUrl(),
                document.getPublicId(),
                document.getFileName(),
                document.isDefault(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }

    public void updateEntity(JobSeeker jobSeeker, JobSeekerRequest request) {
        if (request.firstName() != null && !request.firstName().isBlank()) {
            jobSeeker.setFirstName(request.firstName());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            jobSeeker.setLastName(request.lastName());
        }
        if (request.phone() != null) {
            jobSeeker.setPhone(request.phone());
        }
        if (request.bio() != null) {
            jobSeeker.setBio(request.bio());
        }
        if (request.currentLocation() != null) {
            jobSeeker.setCurrentLocation(request.currentLocation());
        }
        if (request.educationLevel() != null) {
            jobSeeker.setEducationLevel(request.educationLevel());
        }
        if (request.experienceLevel() != null) {
            jobSeeker.setExperienceLevel(request.experienceLevel());
        }
        if (request.preferredJobType() != null) {
            jobSeeker.setPreferredJobType(request.preferredJobType());
        }
    }
}