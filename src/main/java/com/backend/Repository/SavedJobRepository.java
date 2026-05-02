package com.backend.Repository;

import com.backend.Entity.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SavedJobRepository extends JpaRepository<SavedJob, UUID> {

    Page<SavedJob> findByJobSeeker_UserId(UUID jobSeekerId, Pageable pageable);

    Page<SavedJob> findByJobSeeker_UserIdAndBookmarkedTrue(UUID jobSeekerId, Pageable pageable);

    Optional<SavedJob> findByJobSeeker_UserIdAndJob_JobId(UUID jobSeekerId, UUID jobId);

    boolean existsByJobSeeker_UserIdAndJob_JobId(UUID jobSeekerId, UUID jobId);

    void deleteByJobSeeker_UserIdAndJob_JobId(UUID jobSeekerId, UUID jobId);
}