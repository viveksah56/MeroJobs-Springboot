package com.backend.Entity;

import com.backend.Enum.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "jobs", indexes = {
        @Index(name = "idx_job_status",   columnList = "status"),
        @Index(name = "idx_job_employee", columnList = "employee_id"),
        @Index(name = "idx_job_category", columnList = "category_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID jobId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal salaryMin;

    @Column(nullable = false)
    private BigDecimal salaryMax;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkLocationType workLocationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EducationLevel educationLevel;

    @Column(nullable = false)
    private int experienceYears;

    @Column(nullable = false)
    private Instant applicationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    private String location;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    private Instant approvedAt;
    private Instant rejectedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee postedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "job_skills",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<JobApplication> applications = new HashSet<>();
}