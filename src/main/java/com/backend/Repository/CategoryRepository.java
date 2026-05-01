package com.backend.Repository;

import com.backend.Entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT c FROM Category c WHERE c.deleted = false AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Category> searchAllCategories(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.deleted = false AND c.active = true AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Category> searchActiveCategories(@Param("search") String search, Pageable pageable);
}