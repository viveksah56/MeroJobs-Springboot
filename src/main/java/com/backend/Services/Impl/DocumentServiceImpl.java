package com.backend.Services.Impl;

import com.backend.Dto.Request.DocumentUploadRequest;
import com.backend.Dto.Response.DocumentResponse;
import com.backend.Entity.Document;
import com.backend.Entity.JobSeeker;
import com.backend.Enum.DocumentType;
import com.backend.Exception.ResourceNotFoundException;
import com.backend.Repository.DocumentRepository;
import com.backend.Repository.JobSeekerRepository;
import com.backend.Services.DocumentService;
import com.backend.Services.FileService;
import com.backend.Services.FileService.FileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository  documentRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final FileService         fileService;

    private static final String FOLDER = "job-seekers/documents";

    @Override
    public DocumentResponse uploadDocument(DocumentUploadRequest request) {
        JobSeeker jobSeeker = getCurrentJobSeeker();

        FileResponse fileResponse = fileService.uploadFileToCloudinary(
                request.file(),
                FOLDER + "/" + request.documentType().name().toLowerCase()
        );

        if (request.isDefault()) {
            documentRepository.clearDefaultByType(jobSeeker.getUserId(), request.documentType());
        }

        Document document = Document.builder()
                .jobSeeker(jobSeeker)
                .fileUrl(fileResponse.secureUrl())
                .publicId(fileResponse.publicId())
                .fileName(request.file().getOriginalFilename())
                .documentType(request.documentType())
                .isDefault(request.isDefault())
                .build();

        Document saved = documentRepository.save(document);
        log.info("Document '{}' uploaded for jobSeeker '{}'", request.documentType(), jobSeeker.getEmail());
        return toResponse(saved);
    }

    @Override
    public DocumentResponse updateDocument(UUID documentId, DocumentUploadRequest request) {
        JobSeeker jobSeeker = getCurrentJobSeeker();
        Document document   = findDocument(documentId);

        assertOwnership(document, jobSeeker);

        fileService.deleteImageFromCloudinary(document.getPublicId());

        FileResponse fileResponse = fileService.uploadFileToCloudinary(
                request.file(),
                FOLDER + "/" + request.documentType().name().toLowerCase()
        );

        if (request.isDefault()) {
            documentRepository.clearDefaultByType(jobSeeker.getUserId(), request.documentType());
        }

        document.setFileUrl(fileResponse.secureUrl());
        document.setPublicId(fileResponse.publicId());
        document.setFileName(request.file().getOriginalFilename());
        document.setDocumentType(request.documentType());
        document.setDefault(request.isDefault());

        Document updated = documentRepository.save(document);
        log.info("Document '{}' updated for jobSeeker '{}'", documentId, jobSeeker.getEmail());
        return toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getAllDocuments() {
        JobSeeker jobSeeker = getCurrentJobSeeker();
        return documentRepository
                .findAllByJobSeekerUserIdAndDeletedFalse(jobSeeker.getUserId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByType(DocumentType documentType) {
        JobSeeker jobSeeker = getCurrentJobSeeker();
        return documentRepository
                .findAllByJobSeekerUserIdAndDocumentTypeAndDeletedFalse(
                        jobSeeker.getUserId(), documentType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDefaultDocument(DocumentType documentType) {
        JobSeeker jobSeeker = getCurrentJobSeeker();
        Document document = documentRepository
                .findByJobSeekerUserIdAndDocumentTypeAndIsDefaultTrueAndDeletedFalse(
                        jobSeeker.getUserId(), documentType)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No default document found for type: " + documentType));
        return toResponse(document);
    }

    @Override
    public DocumentResponse setDefaultDocument(UUID documentId) {
        JobSeeker jobSeeker = getCurrentJobSeeker();
        Document document   = findDocument(documentId);

        assertOwnership(document, jobSeeker);

        documentRepository.clearDefaultByType(jobSeeker.getUserId(), document.getDocumentType());
        document.setDefault(true);

        Document updated = documentRepository.save(document);
        log.info("Document '{}' set as default for jobSeeker '{}'", documentId, jobSeeker.getEmail());
        return toResponse(updated);
    }

    @Override
    public void deleteDocument(UUID documentId) {
        JobSeeker jobSeeker = getCurrentJobSeeker();
        Document document   = findDocument(documentId);

        assertOwnership(document, jobSeeker);

        fileService.deleteImageFromCloudinary(document.getPublicId());

        document.setDeleted(true);
        document.setDeletedAt(Instant.now());
        document.setDeletedBy(jobSeeker.getEmail());
        documentRepository.save(document);

        log.info("Document '{}' deleted for jobSeeker '{}'", documentId, jobSeeker.getEmail());
    }

    private DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.getDocumentId(),
                document.getJobSeeker().getUserId(),
                document.getDocumentType(),
                document.getFileUrl(),
                document.getPublicId(),
                document.getFileName(),
                document.isDefault(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }

    private Document findDocument(UUID documentId) {
        return documentRepository.findByDocumentIdAndDeletedFalse(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document not found with id: " + documentId));
    }

    private void assertOwnership(Document document, JobSeeker jobSeeker) {
        if (!document.getJobSeeker().getUserId().equals(jobSeeker.getUserId())) {
            throw new ResourceNotFoundException("Document does not belong to you");
        }
    }

    private JobSeeker getCurrentJobSeeker() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return jobSeekerRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "JobSeeker not found: " + email));
    }
}