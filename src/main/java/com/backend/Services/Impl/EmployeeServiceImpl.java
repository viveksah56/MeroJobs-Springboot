package com.backend.Services.Impl;

import com.backend.Dto.Request.EmployeeRequest;
import com.backend.Dto.Response.EmployeeResponse;
import com.backend.Dto.Response.PaginationResponse;
import com.backend.Entity.Employee;
import com.backend.Enum.RoleType;
import com.backend.Mapper.EmployeeMapper;
import com.backend.Repository.EmployeeRepository;
import com.backend.Repository.RoleRepository;
import com.backend.Services.EmployeeService;
import com.backend.Services.FileService;
import com.backend.Services.FileService.FileResponse;
import com.backend.Util.PageRequestBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final RoleRepository roleRepository;

    private static final String FOLDER = "employees/profile-pictures";

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        log.info("Creating employee with email: {}", request.email());

        if (employeeRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already in use: " + request.email());
        }

        FileResponse fileResponse = uploadIfPresent(request.profilePicture());

        Employee employee = employeeMapper.toEntity(request, fileResponse);
        employee.setPassword(passwordEncoder.encode(request.password()));

        roleRepository.findByName(RoleType.EMPLOYEE).ifPresent(role ->
                employee.setRoles(Set.of(role))
        );

        Employee saved = employeeRepository.save(employee);
        log.info("Employee created successfully with id: {}", saved.getUserId());

        return employeeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(UUID id) {
        log.info("Fetching employee with id: {}", id);
        return employeeMapper.toResponse(findActiveById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<EmployeeResponse> getAllEmployees(int page, int size, String sortBy, String sortDir, String search) {
        log.info("Fetching employees - page: {}, size: {}, sortBy: {}, sortDir: {}, search: {}", page, size, sortBy, sortDir, search);

        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();

        Pageable pageable = PageRequestBuilder.build(page, size, sortBy, sortDir);
        Page<Employee> employeePage = employeeRepository.searchAllEmployees(normalizedSearch, pageable);

        return new PaginationResponse<>(
                employeePage.getContent().stream().map(employeeMapper::toResponse).toList(),
                page,
                size,
                employeePage.getTotalElements(),
                employeePage.getTotalPages(),
                sortBy,
                sortDir
        );
    }

    @Override
    public EmployeeResponse updateEmployee(UUID id, EmployeeRequest request) {
        log.info("Updating employee with id: {}", id);

        Employee employee = findActiveById(id);

        if (!employee.getEmail().equals(request.email())
                && employeeRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already in use: " + request.email());
        }

        if (request.profilePicture() != null && !request.profilePicture().isEmpty()) {
            if (employee.getProfilePictureUrl() != null) {
                fileService.deleteImageFromCloudinary(employee.getProfilePictureUrl());
            }
            FileResponse fileResponse = fileService.uploadFileToCloudinary(request.profilePicture(), FOLDER);
            employee.setProfilePictureUrl(fileResponse.secureUrl());
            log.info("Profile picture updated for employee id: {}", id);
        }

        employeeMapper.updateEntity(employee, request);

        if (request.password() != null && !request.password().isBlank()) {
            employee.setPassword(passwordEncoder.encode(request.password()));
        }

        Employee updated = employeeRepository.save(employee);
        log.info("Employee updated successfully with id: {}", id);

        return employeeMapper.toResponse(updated);
    }

    @Override
    public void deleteEmployee(UUID id) {
        log.info("Soft deleting employee with id: {}", id);

        Employee employee = findActiveById(id);
        employee.setDeleted(true);
        employee.setDeletedAt(Instant.now());
        employee.setDeletedBy(getCurrentUsername());

        employeeRepository.save(employee);
        log.info("Employee soft deleted successfully with id: {}", id);
    }

    private FileResponse uploadIfPresent(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            log.info("Uploading profile picture to Cloudinary");
            return fileService.uploadFileToCloudinary(file, FOLDER);
        }
        return null;
    }

    private Employee findActiveById(UUID id) {
        return employeeRepository.findByUserIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    private String getCurrentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return "system";
        return auth.getName();
    }
}