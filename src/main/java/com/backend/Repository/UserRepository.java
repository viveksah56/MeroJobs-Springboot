package com.backend.Repository;

import com.backend.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserIdAndDeletedFalse(UUID id);

    boolean existsByEmail(String email);

    @Query("""
            SELECT u FROM User u
            WHERE u.deleted = false
            AND (
                :search IS NULL
                OR :search = ''
                OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                OR LOWER(u.lastName)  LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                OR LOWER(u.email)     LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
            )
            """)
    Page<User> searchAllUsers(@Param("search") String search, Pageable pageable);
}
