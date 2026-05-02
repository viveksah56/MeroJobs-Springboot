package com.backend.Services;

import com.backend.Dto.Request.DocumentUploadRequest;
import com.backend.Dto.Response.DocumentResponse;
import com.backend.Enum.DocumentType;

import java.util.List;
import java.util.UUID;

public interface DocumentService {

    DocumentResponse uploadDocument(DocumentUploadRequest request);

    DocumentResponse updateDocument(UUID documentId, DocumentUploadRequest request);

    List<DocumentResponse> getAllDocuments();

    List<DocumentResponse> getDocumentsByType(DocumentType documentType);

    DocumentResponse getDefaultDocument(DocumentType documentType);

    DocumentResponse setDefaultDocument(UUID documentId);

    void deleteDocument(UUID documentId);
}