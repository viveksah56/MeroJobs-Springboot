package com.backend.Repository;

import com.backend.Entity.Employee;
import com.backend.Enum.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByEmailAndDeletedFalse(String email);

    Optional<Employee> findByUserIdAndDeletedFalse(UUID userId);

    boolean existsByEmail(String email);

    boolean existsByCompanyName(String companyName);

    @Query("""
            SELECT e FROM Employee e
            WHERE e.deleted = false
            AND (:status IS NULL OR e.status = :status)
            AND (
                :search IS NULL OR :search = ''
                OR LOWER(e.firstName)   LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                OR LOWER(e.lastName)    LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                OR LOWER(e.email)       LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                OR LOWER(e.companyName) LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                OR LOWER(e.jobTitle)    LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                OR LOWER(e.department)  LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
            )
            """)
    Page<Employee> searchAllEmployees(@Param("search") String search,
                                      @Param("status") AccountStatus status,
                                      Pageable pageable);

    @Modifying
    @Query("""
            UPDATE User u
            SET u.status = :status
            WHERE u.userId = :userId
            """)
    void updateStatus(@Param("userId") UUID userId,
                      @Param("status") AccountStatus status);

    @Query("""
            SELECT COUNT(e) FROM Employee e
            WHERE e.deleted = false
            """)
    long countActiveEmployees();

    @Query("""
            SELECT e FROM Employee e
            WHERE e.deleted = false
            AND e.companyName = :companyName
            """)
    Page<Employee> findByCompanyName(@Param("companyName") String companyName, Pageable pageable);
@Query("""
        SELECT e FROM Employee e
                    WHERE e.deleted = false
                    AND (:search IS NULL OR :search = ''
                        OR LOWER(e.firstName)   LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                        OR LOWER(e.lastName)    LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                        OR LOWER(e.email)       LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                        OR LOWER(e.companyName) LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                        OR LOWER(e.jobTitle)    LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                        OR LOWER(e.department)  LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                    )
        """)
    Page<Employee> searchAllEmployees(String search, Pageable pageable);
}