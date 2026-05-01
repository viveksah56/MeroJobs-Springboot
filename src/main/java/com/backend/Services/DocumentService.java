package com.backend.Services;

import com.backend.Dto.Request.DocumentUploadRequest;
import com.backend.Dto.Response.DocumentResponse;
import com.backend.Enum.DocumentType;

import java.util.List;
import java.util.UUID;

public interface DocumentService {

    DocumentResponse uploadDocument(DocumentUploadRequest request);

    DocumentResponse updateDocument(UUID jobSeekerId, UUID documentId, DocumentUploadRequest request);

    List<DocumentResponse> getAllDocuments(UUID jobSeekerId);

    List<DocumentResponse> getDocumentsByType(UUID jobSeekerId, DocumentType documentType);

    DocumentResponse getDefaultDocument(UUID jobSeekerId, DocumentType documentType);

    DocumentResponse setDefaultDocument(UUID jobSeekerId, UUID documentId);

    void deleteDocument(UUID jobSeekerId, UUID documentId);
}