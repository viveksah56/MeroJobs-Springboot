package com.backend.Controller;

import com.backend.Dto.Request.CategoryRequest;
import com.backend.Dto.Response.ApiResponse;
import com.backend.Dto.Response.CategoryResponse;
import com.backend.Dto.Response.PaginationResponse;
import com.backend.Services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(categoryService.createCategory(request), "Category created successfully", HttpStatus.CREATED));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<CategoryResponse>>> getAllCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories(page, size, sortBy, sortDir, search)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<PaginationResponse<CategoryResponse>>> getAllActive(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllActive(page, size, sortBy, sortDir, search)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.updateCategory(id, request), "Category updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted successfully"));
    }
}