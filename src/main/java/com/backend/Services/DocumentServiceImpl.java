package com.backend.Services;

import com.backend.Dto.Request.DocumentUploadRequest;
import com.backend.Dto.Response.DocumentResponse;
import com.backend.Entity.Document;
import com.backend.Entity.JobSeeker;
import com.backend.Enum.DocumentType;
import com.backend.Exception.ResourceNotFoundException;
import com.backend.Mapper.JobSeekerMapper;
import com.backend.Repository.DocumentRepository;
import com.backend.Repository.JobSeekerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final FileService fileService;
    private final JobSeekerMapper jobSeekerMapper;

    private static final String FOLDER = "Job-seekers/Documents";

    @Override
    @Transactional
    public DocumentResponse uploadDocument(DocumentUploadRequest request) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(request.jobSeekerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job seeker not found with id: " + request.jobSeekerId()));

        FileService.FileResponse fileResponse = fileService.uploadFileToCloudinary(
                request.file(), FOLDER);

        if (request.isDefault()) {
            clearExistingDefault(request.jobSeekerId(), request.documentType());
        }

        Document document = Document.builder()
                .jobSeeker(jobSeeker)
                .fileUrl(fileResponse.secureUrl())
                .publicId(fileResponse.publicId())
                .fileName(request.file().getOriginalFilename())
                .documentType(request.documentType())
                .isDefault(request.isDefault())
                .build();

        return jobSeekerMapper.toDocumentResponse(documentRepository.save(document));
    }

    @Override
    @Transactional
    public DocumentResponse updateDocument(UUID jobSeekerId, UUID documentId, DocumentUploadRequest request) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document not found with id: " + documentId));

        if (!document.getJobSeeker().getUserId().equals(jobSeekerId)) {
            throw new ResourceNotFoundException("Document does not belong to this job seeker");
        }

        fileService.deleteImageFromCloudinary(document.getPublicId());

        FileService.FileResponse fileResponse = fileService.uploadFileToCloudinary(
                request.file(), FOLDER);

        if (request.isDefault()) {
            clearExistingDefault(jobSeekerId, document.getDocumentType());
        }

        document.setFileUrl(fileResponse.secureUrl());
        document.setPublicId(fileResponse.publicId());
        document.setFileName(request.file().getOriginalFilename());
        document.setDocumentType(request.documentType());
        document.setDefault(request.isDefault());

        return jobSeekerMapper.toDocumentResponse(documentRepository.save(document));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getAllDocuments(UUID jobSeekerId) {
        if (!jobSeekerRepository.existsById(jobSeekerId)) {
            throw new ResourceNotFoundException(
                    "Job seeker not found with id: " + jobSeekerId);
        }

        return documentRepository.findByJobSeeker_UserId(jobSeekerId)
                .stream()
                .map(jobSeekerMapper::toDocumentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByType(UUID jobSeekerId, DocumentType documentType) {
        if (!jobSeekerRepository.existsById(jobSeekerId)) {
            throw new ResourceNotFoundException(
                    "Job seeker not found with id: " + jobSeekerId);
        }

        return documentRepository.findByJobSeeker_UserIdAndDocumentType(jobSeekerId, documentType)
                .stream()
                .map(jobSeekerMapper::toDocumentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDefaultDocument(UUID jobSeekerId, DocumentType documentType) {
        if (!jobSeekerRepository.existsById(jobSeekerId)) {
            throw new ResourceNotFoundException(
                    "Job seeker not found with id: " + jobSeekerId);
        }

        return documentRepository
                .findByJobSeeker_UserIdAndIsDefaultTrueAndDocumentType(jobSeekerId, documentType)
                .map(jobSeekerMapper::toDocumentResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No default " + documentType + " found for job seeker: " + jobSeekerId));
    }

    @Override
    @Transactional
    public DocumentResponse setDefaultDocument(UUID jobSeekerId, UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document not found with id: " + documentId));

        if (!document.getJobSeeker().getUserId().equals(jobSeekerId)) {
            throw new ResourceNotFoundException("Document does not belong to this job seeker");
        }

        clearExistingDefault(jobSeekerId, document.getDocumentType());

        document.setDefault(true);
        return jobSeekerMapper.toDocumentResponse(documentRepository.save(document));
    }

    @Override
    @Transactional
    public void deleteDocument(UUID jobSeekerId, UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document not found with id: " + documentId));

        if (!document.getJobSeeker().getUserId().equals(jobSeekerId)) {
            throw new ResourceNotFoundException("Document does not belong to this job seeker");
        }

        fileService.deleteImageFromCloudinary(document.getPublicId());
        documentRepository.delete(document);
    }

    private void clearExistingDefault(UUID jobSeekerId, DocumentType documentType) {
        documentRepository
                .findByJobSeeker_UserIdAndIsDefaultTrueAndDocumentType(jobSeekerId, documentType)
                .ifPresent(existing -> {
                    existing.setDefault(false);
                    documentRepository.save(existing);
                });
    }
}