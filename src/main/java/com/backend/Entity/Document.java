package com.backend.Entity;

import com.backend.Enum.DocumentType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(
        name = "documents",
        indexes = {
                @Index(name = "idx_document_seeker", columnList = "job_seeker_id"),
                @Index(name = "idx_document_type", columnList = "document_type")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID documentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false, length = 512)
    private String publicId;

    @Column(nullable = false)
    private String fileName;

    @Builder.Default
    @Column(nullable = false)
    private boolean isDefault = false;
}