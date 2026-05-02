package com.backend.Repository;

import com.backend.Entity.JobSeeker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JobSeekerRepository extends JpaRepository<JobSeeker, UUID> {

    boolean existsByEmail(String email);

    Optional<JobSeeker> findByEmail(String email);

    Optional<JobSeeker> findByEmailAndDeletedFalse(String email);

    Page<JobSeeker> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email, Pageable pageable);

    Page<JobSeeker> findBySkills_SkillId(UUID skillId, Pageable pageable);

    Page<JobSeeker> findBySkills_SkillIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            UUID skillId, String firstName, String lastName, Pageable pageable);
}