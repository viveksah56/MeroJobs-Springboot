package com.backend.Services;

import com.backend.Dto.Response.PaginationResponse;
import com.backend.Dto.Response.UserResponse;

import java.util.UUID;

public interface UserService {

    UserResponse getUserById(UUID id);

    PaginationResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDir, String search);

    void deleteUser(UUID id);
}