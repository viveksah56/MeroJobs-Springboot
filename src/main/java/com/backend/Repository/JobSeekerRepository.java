package com.backend.Repository;

import com.backend.Entity.JobSeeker;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobSeekerRepository extends JpaRepository<JobSeeker, UUID> {
    Optional<JobSeeker> findByEmail(String email);
    Optional<JobSeeker> findByUserId(UUID userId);

    Optional<JobSeeker> findByEmailAndDeletedFalse(String email);

    Optional<JobSeeker> findByUserIdAndDeletedFalse(UUID userId);

    boolean existsByEmail(String email);



}
