package com.backend.Entity;

import com.backend.Enum.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "job_applications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "job_seeker_id"}),
        indexes = {
                @Index(name = "idx_application_job", columnList = "job_id"),
                @Index(name = "idx_application_seeker", columnList = "job_seeker_id"),
                @Index(name = "idx_application_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JobApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID applicationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Document resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cover_letter_id", nullable = true)
    private Document coverLetter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String employeeNote;

    @Column(columnDefinition = "TEXT")
    private String adminNote;

    private Instant reviewedAt;
    private Instant decidedAt;
}