package com.backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_seekers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JobSeeker extends User {

    private String currentLocation;
    private String educationLevel;
    private String experienceLevel;
    private String preferredJobType;
    @ManyToMany
    @JoinTable(
            name = "job_seeker_skills",
            joinColumns = @JoinColumn(name = "job_seeker_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();
    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<JobApplication> applications = new ArrayList<>();

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();
}