package com.backend.Controller;

import com.backend.Dto.Request.DocumentUploadRequest;
import com.backend.Dto.Response.ApiResponse;
import com.backend.Dto.Response.DocumentResponse;
import com.backend.Enum.DocumentType;
import com.backend.Services.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/job-seekers/{jobSeekerId}/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @PathVariable UUID jobSeekerId,
            @Valid @ModelAttribute DocumentUploadRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        documentService.uploadDocument(request),
                        "Document uploaded successfully"
                ));
    }

    @PutMapping(value = "/{documentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponse>> updateDocument(
            @PathVariable UUID jobSeekerId,
            @PathVariable UUID documentId,
            @Valid @ModelAttribute DocumentUploadRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                documentService.updateDocument(jobSeekerId, documentId, request),
                "Document updated successfully"
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getAllDocuments(
            @PathVariable UUID jobSeekerId) {
        return ResponseEntity.ok(ApiResponse.success(
                documentService.getAllDocuments(jobSeekerId),
                "Documents retrieved successfully"
        ));
    }

    @GetMapping("/type/{documentType}")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocumentsByType(
            @PathVariable UUID jobSeekerId,
            @PathVariable DocumentType documentType) {
        return ResponseEntity.ok(ApiResponse.success(
                documentService.getDocumentsByType(jobSeekerId, documentType),
                "Documents retrieved successfully"
        ));
    }

    @GetMapping("/type/{documentType}/default")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDefaultDocument(
            @PathVariable UUID jobSeekerId,
            @PathVariable DocumentType documentType) {
        return ResponseEntity.ok(ApiResponse.success(
                documentService.getDefaultDocument(jobSeekerId, documentType),
                "Default document retrieved successfully"
        ));
    }

    @PatchMapping("/{documentId}/default")
    public ResponseEntity<ApiResponse<DocumentResponse>> setDefaultDocument(
            @PathVariable UUID jobSeekerId,
            @PathVariable UUID documentId) {
        return ResponseEntity.ok(ApiResponse.success(
                documentService.setDefaultDocument(jobSeekerId, documentId),
                "Default document set successfully"
        ));
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable UUID jobSeekerId,
            @PathVariable UUID documentId) {
        documentService.deleteDocument(jobSeekerId, documentId);
        return ResponseEntity.ok(ApiResponse.success(null, "Document deleted successfully"));
    }
}