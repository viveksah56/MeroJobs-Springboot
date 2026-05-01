package com.backend.Controller;

import com.backend.Dto.Request.LoginRequest;
import com.backend.Dto.Response.ApiResponse;
import com.backend.Dto.Response.LoginResponse;
import com.backend.Dto.Response.UserResponse;
import com.backend.Services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request,
                                                            HttpServletRequest httpRequest,
                                                            HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request, httpRequest, response);
        //log cookie
        log.info("Cookie: {}", response.getHeaders("Set-Cookie"));
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getLoggedInUser() {
        UserResponse userResponse = authService.getLoggedInUser();
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }



}