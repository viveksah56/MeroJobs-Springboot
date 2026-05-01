package com.backend.Repository;

import com.backend.Entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    Optional<Skill> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Skill> findAllBySkillIdIn(List<UUID> skillIds);

    @Query("""
            SELECT s FROM Skill s
            WHERE s.deleted = false
            AND (:categoryId IS NULL OR s.category.categoryId = :categoryId)
            AND (:search IS NULL OR :search = ''
                OR LOWER(s.name) LIKE LOWER(CONCAT('%', TRIM(:search), '%')))
            """)
    Page<Skill> searchSkills(@Param("search") String search,
                             @Param("categoryId") UUID categoryId,
                             Pageable pageable);

    @Query("""
            SELECT s FROM Skill s
            LEFT JOIN FETCH s.category c
            WHERE (:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Skill> searchAllSkills(@Param("search") String search, Pageable pageable);

    List<Skill> findByCategoryCategoryId(UUID categoryId);
}
