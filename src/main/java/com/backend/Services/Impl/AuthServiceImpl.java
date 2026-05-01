package com.backend.Services.Impl;

import com.backend.Dto.Request.LoginRequest;
import com.backend.Dto.Response.ActiveSessionsResponse;
import com.backend.Dto.Response.LoginResponse;
import com.backend.Dto.Response.UserResponse;
import com.backend.Entity.RefreshToken;
import com.backend.Entity.User;
import com.backend.Entity.UserDevice;
import com.backend.Mapper.UserMapper;
import com.backend.Repository.RefreshTokenRepository;
import com.backend.Repository.UserDeviceRepository;
import com.backend.Repository.UserRepository;
import com.backend.Services.AuthService;
import com.backend.Services.JwtService;
import com.backend.Services.RedisService;
import com.backend.Util.DeviceInfoExtractor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository         userRepository;
    private final UserDeviceRepository   userDeviceRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService             jwtService;
    private final RedisService           redisService;
    private final AuthenticationManager  authenticationManager;
    private final UserDetailsService     userDetailsService;
    private final UserMapper             userMapper;
    private final DeviceInfoExtractor    deviceInfoExtractor;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    @Override
    public LoginResponse login(LoginRequest request,
                               HttpServletRequest httpRequest,
                               HttpServletResponse httpResponse) {
        log.info("Login attempt for email: {}", request.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email().trim().toLowerCase(),
                        request.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = findUserByAuth(authentication);
        UserDevice device = resolveDevice(user, httpRequest);

        refreshTokenRepository.revokeAllByDeviceId(device.getUserDeviceId());

        String accessToken  = jwtService.generateAccessToken(authentication, buildClaims(user));
        String refreshToken = jwtService.generateRefreshToken(authentication);

        saveRefreshToken(refreshToken, device);
        setRefreshTokenCookie(httpResponse, refreshToken);

        log.info("Login successful for email: {} on device: {}", user.getEmail(), device.getDeviceId());

        return LoginResponse.builder().token(accessToken).build();
    }

    @Override
    public LoginResponse refresh(HttpServletRequest httpRequest,
                                 HttpServletResponse httpResponse) {
        String rawToken = extractRefreshTokenFromCookie(httpRequest);

        RefreshToken storedToken = refreshTokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (!storedToken.isValid()) {
            refreshTokenRepository.revokeAllByDeviceId(storedToken.getUserDevice().getUserDeviceId());
            throw new RuntimeException("Refresh token is invalid or expired. All sessions revoked.");
        }

        storedToken.markUsed();

        String email = jwtService.extractUsername(rawToken);
        User user    = findUserByEmail(email);

        UserDetails userDetails   = userDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String newAccessToken  = jwtService.generateAccessToken(authentication, buildClaims(user));
        String newRefreshToken = jwtService.generateRefreshToken(authentication);

        storedToken.revoke(newRefreshToken);

        UserDevice device = storedToken.getUserDevice();
        device.setLastSeenAt(Instant.now());
        device.setIpAddress(deviceInfoExtractor.extractIp(httpRequest));

        saveRefreshToken(newRefreshToken, device);
        setRefreshTokenCookie(httpResponse, newRefreshToken);

        log.info("Refresh token rotated for email: {}", email);

        return LoginResponse.builder().token(newAccessToken).build();
    }

    @Override
    public void logout(String accessToken,
                       HttpServletRequest httpRequest,
                       HttpServletResponse httpResponse) {
        User user = findUserByToken(accessToken);

        blacklistToken(accessToken);
        refreshTokenRepository.revokeAllByUserId(user.getUserId());
        clearRefreshTokenCookie(httpResponse);

        log.info("Logout successful for email: {}", user.getEmail());
    }

    @Override
    public void logoutDevice(UUID deviceId, String accessToken) {
        User user = findUserByToken(accessToken);

        UserDevice device = userDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        if (!device.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Device does not belong to this user");
        }

        refreshTokenRepository.revokeAllByDeviceId(deviceId);
        userDeviceRepository.deactivateDevice(user.getUserId(), device.getDeviceId());

        log.info("Logged out device '{}' for user '{}'", deviceId, user.getEmail());
    }

    @Override
    public void logoutAllDevices(String accessToken, HttpServletResponse httpResponse) {
        User user = findUserByToken(accessToken);

        blacklistToken(accessToken);
        refreshTokenRepository.revokeAllByUserId(user.getUserId());
        userDeviceRepository.deactivateAllDevices(user.getUserId());
        clearRefreshTokenCookie(httpResponse);

        log.info("Logged out all devices for user '{}'", user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActiveSessionsResponse> getActiveSessions(String accessToken,
                                                          HttpServletRequest httpRequest) {
        User user              = findUserByToken(accessToken);
        String currentDeviceId = deviceInfoExtractor.extractDeviceId(httpRequest);

        return userDeviceRepository.findAllByUserUserIdAndActiveTrue(user.getUserId())
                .stream()
                .map(device -> new ActiveSessionsResponse(
                        device.getUserDeviceId(),
                        device.getDeviceId(),
                        device.getDeviceType(),
                        device.getOs(),
                        device.getBrowser(),
                        device.getIpAddress(),
                        device.isTrusted(),
                        device.getFirstSeenAt(),
                        device.getLastSeenAt(),
                        device.getDeviceId().equals(currentDeviceId)
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getLoggedInUser() {
        String email = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()
        ).getName();
        return userMapper.toResponse(findUserByEmail(email));
    }

    private UserDevice resolveDevice(User user, HttpServletRequest httpRequest) {
        String deviceId   = deviceInfoExtractor.extractDeviceId(httpRequest);
        String ipAddress  = deviceInfoExtractor.extractIp(httpRequest);
        String userAgent  = deviceInfoExtractor.extractUserAgentString(httpRequest);
        String deviceType = deviceInfoExtractor.extractDeviceType(httpRequest);
        String os         = deviceInfoExtractor.extractOs(httpRequest);
        String browser    = deviceInfoExtractor.extractBrowser(httpRequest);

        log.info("Device - id: {}, ip: {}, type: {}, os: {}, browser: {}",
                deviceId, ipAddress, deviceType, os, browser);

        return userDeviceRepository
                .findByUserUserIdAndDeviceId(user.getUserId(), deviceId)
                .map(existing -> {
                    existing.setIpAddress(ipAddress);
                    existing.setUserAgent(userAgent);
                    existing.setDeviceType(deviceType);
                    existing.setOs(os);
                    existing.setBrowser(browser);
                    existing.setLastSeenAt(Instant.now());
                    existing.setActive(true);
                    return userDeviceRepository.save(existing);
                })
                .orElseGet(() -> userDeviceRepository.save(
                        UserDevice.builder()
                                .deviceId(deviceId)
                                .deviceType(deviceType)
                                .os(os)
                                .browser(browser)
                                .userAgent(userAgent)
                                .ipAddress(ipAddress)
                                .user(user)
                                .active(true)
                                .trusted(false)
                                .build()
                ));
    }

    private void saveRefreshToken(String token, UserDevice device) {
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .token(token)
                        .expiresAt(Instant.now().plusMillis(refreshTokenExpiration))
                        .userDevice(device)
                        .build()
        );
    }

    private Map<String, Object> buildClaims(User user) {
        return Map.of(
                "roles",  user.getRoles().stream().map(r -> r.getName().name()).toList(),
                "userId", user.getUserId().toString()
        );
    }

    private void blacklistToken(String accessToken) {
        long remainingTtl = jwtService.getRemainingExpiration(accessToken);
        if (remainingTtl > 0) {
            redisService.blacklistAccessToken(accessToken, remainingTtl);
        }
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        response.addHeader("Set-Cookie",
                buildCookie(token, refreshTokenExpiration / 1000).toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        response.addHeader("Set-Cookie", buildCookie("", 0).toString());
    }

    private ResponseCookie buildCookie(String value, long maxAge) {
        return ResponseCookie.from("refreshToken", value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
    }

    private User findUserByAuth(Authentication authentication) {
        return findUserByEmail(authentication.getName());
    }

    private User findUserByToken(String accessToken) {
        String email = jwtService.extractUsername(accessToken);
        if (email == null) throw new RuntimeException("Invalid access token");
        return findUserByEmail(email);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private static String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new IllegalArgumentException("No cookies found in request");
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token cookie not found"));
    }
}