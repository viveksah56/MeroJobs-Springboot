package com.backend.Services.Impl;

import com.backend.Dto.Request.SkillRequest;
import com.backend.Dto.Response.PaginationResponse;
import com.backend.Dto.Response.SkillResponse;
import com.backend.Entity.Category;
import com.backend.Entity.Skill;
import com.backend.Entity.User;
import com.backend.Mapper.SkillMapper;
import com.backend.Repository.CategoryRepository;
import com.backend.Repository.SkillRepository;
import com.backend.Services.SkillService;
import com.backend.Util.PageRequestBuilder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final CategoryRepository categoryRepository;
    private final SkillMapper skillMapper;

    @Override
    public SkillResponse createSkill(SkillRequest request) {
        if (skillRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException("Skill with name '" + request.name() + "' already exists");
        }

        Skill skill = skillMapper.toEntity(request);
        skill.setCategory(findCategoryById(request.categoryId()));

        return skillMapper.toResponse(skillRepository.save(skill));
    }

    @Override
    @Transactional(readOnly = true)
    public SkillResponse getById(UUID id) {
        return skillMapper.toResponse(findSkillById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<SkillResponse> getAll(int page, int size, String sortBy, String sortDir, String search) {
        Pageable pageable = PageRequestBuilder.build(page, size, sortBy, sortDir);
        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();

        Page<Skill> skillPage = skillRepository.searchAllSkills(normalizedSearch, pageable);

        List<SkillResponse> data = skillPage.getContent()
                .stream()
                .map(skillMapper::toResponse)
                .toList();

        return new PaginationResponse<>(
                data,
                page,
                skillPage.getSize(),
                skillPage.getTotalElements(),
                skillPage.getTotalPages(),
                sortBy,
                sortDir
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getByCategory(UUID categoryId) {
        findCategoryById(categoryId);
        return skillRepository.findByCategoryCategoryId(categoryId)
                .stream()
                .map(skillMapper::toResponse)
                .toList();
    }

    @Override
    public SkillResponse updateSkill(UUID id, SkillRequest request) {
        Skill skill = findSkillById(id);

        if (!skill.getName().equalsIgnoreCase(request.name()) &&
                skillRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException("Skill with name '" + request.name() + "' already exists");
        }

        skill.setName(request.name());
        skill.setCategory(findCategoryById(request.categoryId()));
        if (request.active() != null) skill.setActive(request.active());

        return skillMapper.toResponse(skillRepository.save(skill));
    }

    @Override
    public void delete(UUID id) {
        skillRepository.delete(findSkillById(id));
    }

    @Override
    public SkillResponse toggleActive(UUID id) {
        Skill skill = findSkillById(id);
        skill.setActive(!skill.isActive());
        return skillMapper.toResponse(skillRepository.save(skill));
    }

    private Skill findSkillById(UUID id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found with id: " + id));
    }

    private Category findCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }
}