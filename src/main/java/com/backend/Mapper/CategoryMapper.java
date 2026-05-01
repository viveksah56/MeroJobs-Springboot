package com.backend.Mapper;

import com.backend.Dto.Request.CategoryRequest;
import com.backend.Dto.Response.CategoryResponse;
import com.backend.Entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.name())
                .description(request.description())
                .active(request.active() != null ? request.active() : true)
                .build();
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getCategoryId(),
                category.getName(),
                category.getDescription(),
                category.isActive(),
                category.getCreatedAt(),
                category.getCreatedBy(),
                category.getUpdatedAt(),
                category.getUpdatedBy()
        );
    }
}