package com.backend.Controller;

import com.backend.Dto.Request.SkillRequest;
import com.backend.Dto.Response.ApiResponse;
import com.backend.Dto.Response.PaginationResponse;
import com.backend.Dto.Response.SkillResponse;
import com.backend.Services.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<ApiResponse<SkillResponse>> createSkill(@Valid @RequestBody SkillRequest request) {
        SkillResponse response = skillService.createSkill(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Skill created successfully", HttpStatus.CREATED));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(skillService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<SkillResponse>>> getAllSkills(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(skillService.getAll(page, size, sortBy, sortDir, search)));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getByCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(ApiResponse.success(skillService.getByCategory(categoryId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillResponse>> updateSkill(
            @PathVariable UUID id,
            @Valid @RequestBody SkillRequest request) {
        return ResponseEntity.ok(ApiResponse.success(skillService.updateSkill(id, request), "Skill updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        skillService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Skill deleted successfully"));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<SkillResponse>> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(skillService.toggleActive(id), "Skill status toggled"));
    }
}