package com.backend.Services;

import com.backend.Dto.Request.SkillRequest;
import com.backend.Dto.Response.PaginationResponse;
import com.backend.Dto.Response.SkillResponse;

import java.util.List;
import java.util.UUID;

public interface SkillService {
    SkillResponse createSkill(SkillRequest request);

    SkillResponse getById(UUID id);

    PaginationResponse<SkillResponse> getAll(int page, int size, String sortBy, String sortDir, String search);

    List<SkillResponse> getByCategory(UUID categoryId);

    SkillResponse updateSkill(UUID id, SkillRequest request);

    void delete(UUID id);

    SkillResponse toggleActive(UUID id);
}