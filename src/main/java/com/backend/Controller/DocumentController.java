package com.backend.Controller;

import com.backend.Dto.Request.DocumentUploadRequest;
import com.backend.Dto.Response.ApiResponse;
import com.backend.Dto.Response.DocumentResponse;
import com.backend.Enum.DocumentType;
import com.backend.Services.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('JOB_SEEKER')")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @Valid @ModelAttribute DocumentUploadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        documentService.uploadDocument(request),
                        "Document uploaded successfully",
                        HttpStatus.CREATED));
    }

    @PutMapping(value = "/{documentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponse>> updateDocument(
            @PathVariable UUID documentId,
            @Valid @ModelAttribute DocumentUploadRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                documentService.updateDocument(documentId, request),
                "Document updated successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getAllDocuments() {
        return ResponseEntity.ok(ApiResponse.success(documentService.getAllDocuments()));
    }

    @GetMapping("/type/{documentType}")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocumentsByType(
            @PathVariable DocumentType documentType) {
        return ResponseEntity.ok(ApiResponse.success(
                documentService.getDocumentsByType(documentType)));
    }

    @GetMapping("/type/{documentType}/default")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDefaultDocument(
            @PathVariable DocumentType documentType) {
        return ResponseEntity.ok(ApiResponse.success(
                documentService.getDefaultDocument(documentType)));
    }

    @PatchMapping("/{documentId}/default")
    public ResponseEntity<ApiResponse<DocumentResponse>> setDefaultDocument(
            @PathVariable UUID documentId) {
        return ResponseEntity.ok(ApiResponse.success(
                documentService.setDefaultDocument(documentId),
                "Default document set successfully"));
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable UUID documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.ok(ApiResponse.success(null, "Document deleted successfully"));
    }
}