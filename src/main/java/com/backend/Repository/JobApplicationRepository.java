package com.backend.Repository;

import com.backend.Entity.JobApplication;
import com.backend.Enum.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    Optional<JobApplication> findByApplicationIdAndDeletedFalse(UUID applicationId);

    boolean existsByJobJobIdAndJobSeekerUserId(UUID jobId, UUID jobSeekerId);

    Page<JobApplication> findByJobJobIdAndDeletedFalse(UUID jobId, Pageable pageable);

    Page<JobApplication> findByJobSeekerUserIdAndDeletedFalse(UUID jobSeekerId, Pageable pageable);

    long countByStatus(ApplicationStatus status);

    long countByDeletedFalse();

    @Query("""
            SELECT a FROM JobApplication a
            WHERE a.deleted = false
            AND (:status IS NULL OR a.status = :status)
            AND (:search IS NULL OR :search = ''
                OR LOWER(a.jobSeeker.firstName) LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                OR LOWER(a.jobSeeker.lastName)  LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                OR LOWER(a.job.title)           LIKE LOWER(CONCAT('%', TRIM(:search), '%')))
            """)
    Page<JobApplication> searchApplications(@Param("search") String search,
                                            @Param("status") ApplicationStatus status,
                                            Pageable pageable);
}