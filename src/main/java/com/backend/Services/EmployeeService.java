package com.backend.Services;

import com.backend.Dto.Request.EmployeeRequest;
import com.backend.Dto.Response.EmployeeResponse;
import com.backend.Dto.Response.PaginationResponse;

import java.util.UUID;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse getEmployeeById(UUID id);

    EmployeeResponse updateEmployee(UUID id, EmployeeRequest request);

    void deleteEmployee(UUID id);


    PaginationResponse<EmployeeResponse> getAllEmployees(int page, int size, String sortBy, String sortDir, String search);
}