package com.backend.Repository;

import com.backend.Entity.Document;
import com.backend.Enum.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findByJobSeeker_UserId(UUID jobSeekerId);

    List<Document> findByJobSeeker_UserIdAndDocumentType(UUID jobSeekerId, DocumentType documentType);

    Optional<Document> findByDocumentIdAndJobSeeker_UserId(UUID documentId, UUID jobSeekerId);

    Optional<Document> findByJobSeeker_UserIdAndIsDefaultTrueAndDocumentType(UUID jobSeekerId, DocumentType documentType);

    boolean existsByJobSeeker_UserIdAndDocumentTypeAndIsDefaultTrue(UUID jobSeekerId, DocumentType documentType);

    @Modifying
    @Query("UPDATE Document d SET d.isDefault = false " +
            "WHERE d.jobSeeker.userId = :jobSeekerId AND d.documentType = :documentType")
    void clearDefaultByJobSeekerAndType(@Param("jobSeekerId") UUID jobSeekerId,
                                        @Param("documentType") DocumentType documentType);
}