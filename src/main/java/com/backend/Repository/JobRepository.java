package com.backend.Repository;

import com.backend.Entity.Job;
import com.backend.Enum.ExperienceLevel;
import com.backend.Enum.JobStatus;
import com.backend.Enum.JobType;
import com.backend.Enum.WorkLocationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {


    Optional<Job> findByJobIdAndDeletedFalse(UUID jobId);

    long countByStatus(JobStatus status);

    long countByDeletedFalse();

    @Query("""
            SELECT j FROM Job j
            WHERE j.deleted = false
            AND j.status = :status
            AND (:search IS NULL OR :search = ''
                OR LOWER(j.title)       LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                OR LOWER(j.description) LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                OR LOWER(j.location)    LIKE LOWER(CONCAT('%', TRIM(:search), '%')))
            AND (:jobType IS NULL           OR j.jobType = :jobType)
            AND (:workLocationType IS NULL  OR j.workLocationType = :workLocationType)
            AND (:experienceLevel IS NULL   OR j.experienceLevel = :experienceLevel)
            AND (:categoryId IS NULL        OR j.category.categoryId = :categoryId)
            """)
    Page<Job> searchActiveJobs(@Param("search")           String search,
                               @Param("status")           JobStatus status,
                               @Param("jobType")          JobType jobType,
                               @Param("workLocationType") WorkLocationType workLocationType,
                               @Param("experienceLevel")  ExperienceLevel experienceLevel,
                               @Param("categoryId")       UUID categoryId,
                               Pageable pageable);

    @Query("""
            SELECT j FROM Job j
            WHERE j.deleted = false
            AND j.postedBy.userId = :employeeId
            AND (:status IS NULL OR j.status = :status)
            AND (:search IS NULL OR :search = ''
                OR LOWER(j.title) LIKE LOWER(CONCAT('%', TRIM(:search), '%')))
            """)
    Page<Job> findByEmployeeId(@Param("employeeId") UUID employeeId,
                               @Param("status")     JobStatus status,
                               @Param("search")     String search,
                               Pageable pageable);

    @Query("""
            SELECT j FROM Job j
            WHERE j.deleted = false
            AND (:status IS NULL OR j.status = :status)
            AND (:search IS NULL OR :search = ''
                OR LOWER(j.title)       LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                OR LOWER(j.description) LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                OR LOWER(j.location)    LIKE LOWER(CONCAT('%', TRIM(:search), '%')))
            """)
    Page<Job> searchAllJobs(@Param("search") String search,
                            @Param("status") JobStatus status,
                            Pageable pageable);




}