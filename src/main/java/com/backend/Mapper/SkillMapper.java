package com.backend.Mapper;

import com.backend.Dto.Request.SkillRequest;
import com.backend.Dto.Response.SkillResponse;
import com.backend.Entity.Skill;
import org.springframework.stereotype.Component;

@Component
public class SkillMapper {

    public Skill toEntity(SkillRequest request) {
        return Skill.builder()
                .name(request.name())
                .active(request.active() != null ? request.active() : true)
                .build();
    }

    public SkillResponse toResponse(Skill skill) {
        return new SkillResponse(
                skill.getSkillId(),
                skill.getName(),
                skill.isActive(),
                skill.getCategory() != null ? skill.getCategory().getCategoryId() : null,
                skill.getCategory() != null ? skill.getCategory().getName() : null,
                skill.getCreatedAt(),
                skill.getCreatedBy(),
                skill.getUpdatedAt(),
                skill.getUpdatedBy()
        );
    }
}