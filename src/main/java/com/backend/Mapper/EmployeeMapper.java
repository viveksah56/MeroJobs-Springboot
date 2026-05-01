package com.backend.Mapper;

import com.backend.Dto.Request.EmployeeRequest;
import com.backend.Dto.Response.EmployeeResponse;
import com.backend.Entity.Employee;
import com.backend.Services.FileService.FileResponse;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequest request, FileResponse fileResponse) {
        return Employee.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .profilePictureUrl(fileResponse != null ? fileResponse.secureUrl() : null)
                .bio(request.bio())
                .status(request.status())
                .companyName(request.companyName())
                .jobTitle(request.jobTitle())
                .department(request.department())
                .workLocation(request.workLocation())
                .salary(request.salary())
                .deleted(false)
                .build();
    }

    public EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getUserId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getProfilePictureUrl(),
                employee.getBio(),
                employee.getStatus(),
                employee.getCompanyName(),
                employee.getJobTitle(),
                employee.getDepartment(),
                employee.getWorkLocation(),
                employee.getSalary(),
                mapRoles(employee),
                employee.getCreatedAt(),
                employee.getCreatedBy(),
                employee.getUpdatedAt(),
                employee.getUpdatedBy()
        );
    }

    public void updateEntity(Employee employee, EmployeeRequest request) {
        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setEmail(request.email());
        employee.setPhone(request.phone());
        employee.setBio(request.bio());
        employee.setStatus(request.status());
        employee.setCompanyName(request.companyName());
        employee.setJobTitle(request.jobTitle());
        employee.setDepartment(request.department());
        employee.setWorkLocation(request.workLocation());
        employee.setSalary(request.salary());
    }

    private Set<String> mapRoles(Employee employee) {
        if (employee.getRoles() == null) return Set.of();
        return employee.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}