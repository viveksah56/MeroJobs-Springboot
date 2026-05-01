package com.backend.Services;

import com.backend.Dto.Request.CategoryRequest;
import com.backend.Dto.Response.CategoryResponse;
import com.backend.Dto.Response.PaginationResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse getById(UUID categoryId);

    PaginationResponse<CategoryResponse> getAllCategories(int page, int size, String sortBy, String sortDir, String search);

    PaginationResponse<CategoryResponse> getAllActive(int page, int size, String sortBy, String sortDir, String search);

    CategoryResponse updateCategory(UUID id, CategoryRequest request);

    void deleteCategory(UUID categoryId);
}