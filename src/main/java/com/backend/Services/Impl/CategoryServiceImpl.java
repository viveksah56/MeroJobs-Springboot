package com.backend.Services.Impl;

import com.backend.Dto.Request.CategoryRequest;
import com.backend.Dto.Response.CategoryResponse;
import com.backend.Dto.Response.PaginationResponse;
import com.backend.Entity.Category;
import com.backend.Mapper.CategoryMapper;
import com.backend.Repository.CategoryRepository;
import com.backend.Services.CategoryService;
import com.backend.Util.PageRequestBuilder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException("Category with name '" + request.name() + "' already exists");
        }
        
        return categoryMapper.toResponse(categoryRepository.save(categoryMapper.toEntity(request)));
    }

    @Override
    public CategoryResponse getById(UUID categoryId) {
        return categoryMapper.toResponse(findOrThrow(categoryId));
    }

    @Override
    public PaginationResponse<CategoryResponse> getAllCategories(int page, int size, String sortBy, String sortDir, String search) {
        Pageable pageable = PageRequestBuilder.build(page, size, sortBy, sortDir);
        String normalizedSearch = normalizeSearch(search);
        Page<Category> result = categoryRepository.searchAllCategories(normalizedSearch, pageable);
        return toPaginationResponse(result, page, size, sortBy, sortDir);
    }

    @Override
    public PaginationResponse<CategoryResponse> getAllActive(int page, int size, String sortBy, String sortDir, String search) {
        Pageable pageable = PageRequestBuilder.build(page, size, sortBy, sortDir);
        String normalizedSearch = normalizeSearch(search);
        Page<Category> result = categoryRepository.searchActiveCategories(normalizedSearch, pageable);
        return toPaginationResponse(result, page, size, sortBy, sortDir);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = findOrThrow(id);

        boolean nameConflict = categoryRepository
                .findByNameIgnoreCase(request.name())
                .map(existing -> !existing.getCategoryId().equals(id))
                .orElse(false);

        if (nameConflict) {
            throw new IllegalArgumentException("Category with name '" + request.name() + "' already exists");
        }

        category.setName(request.name());
        category.setDescription(request.description());
        category.setActive(request.active());

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(UUID categoryId) {
        Category category = findOrThrow(categoryId);
        category.setDeleted(true);
        category.setDeletedAt(Instant.now());
        categoryRepository.save(category);
    }

    private Category findOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }

    private String normalizeSearch(String search) {
        return (search == null || search.isBlank()) ? null : search.trim();
    }

    private PaginationResponse<CategoryResponse> toPaginationResponse(Page<Category> page, int pageNum, int size, String sortBy, String sortDir) {
        return new PaginationResponse<>(
                page.getContent().stream().map(categoryMapper::toResponse).toList(),
                pageNum,
                size,
                page.getTotalElements(),
                page.getTotalPages(),
                sortBy,
                sortDir
        );
    }
}