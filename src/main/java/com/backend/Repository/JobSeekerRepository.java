package com.backend.Repository;

import com.backend.Entity.JobSeeker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JobSeekerRepository extends JpaRepository<JobSeeker, UUID> {

    boolean existsByEmail(String email);

    Optional<JobSeeker> findByEmail(String email);
    @Query("SELECT j FROM JobSeeker j LEFT JOIN FETCH j.skills WHERE j.userId = :id")
    Optional<JobSeeker> findByUserId(@Param("id") UUID id);

    Optional<JobSeeker> findByEmailAndDeletedFalse(String email);

    Page<JobSeeker> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email, Pageable pageable);

    Page<JobSeeker> findBySkills_SkillId(UUID skillId, Pageable pageable);

    Page<JobSeeker> findBySkills_SkillIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            UUID skillId, String firstName, String lastName, Pageable pageable);
}