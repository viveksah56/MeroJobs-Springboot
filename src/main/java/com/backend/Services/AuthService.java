package com.backend.Services;

import com.backend.Dto.Request.LoginRequest;
import com.backend.Dto.Response.ActiveSessionsResponse;
import com.backend.Dto.Response.LoginResponse;
import com.backend.Dto.Response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.UUID;

public interface AuthService {

    LoginResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    LoginResponse refresh(HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    void logout(String accessToken, HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    void logoutDevice(UUID deviceId, String accessToken);

    void logoutAllDevices(String accessToken, HttpServletResponse httpResponse);

    List<ActiveSessionsResponse> getActiveSessions(String accessToken, HttpServletRequest httpRequest);

    UserResponse getLoggedInUser();
}