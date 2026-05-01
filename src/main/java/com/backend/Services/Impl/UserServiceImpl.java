package com.backend.Services.Impl;

import com.backend.Dto.Response.PaginationResponse;
import com.backend.Dto.Response.UserResponse;
import com.backend.Entity.User;
import com.backend.Mapper.UserMapper;
import com.backend.Repository.UserRepository;
import com.backend.Services.UserService;
import com.backend.Util.PageRequestBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        log.info("Fetching user with id: {}", id);
        return userMapper.toResponse(findActiveById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDir, String search) {
        log.info("Fetching users - page: {}, size: {}, sortBy: {}, sortDir: {}, search: {}", page, size, sortBy, sortDir, search);

        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();

        Pageable pageable = PageRequestBuilder.build(page, size, sortBy, sortDir);
        Page<User> userPage = userRepository.searchAllUsers(normalizedSearch, pageable);

        return new PaginationResponse<>(
                userPage.getContent().stream().map(userMapper::toResponse).toList(),
                page,
                size,
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                sortBy,
                sortDir
        );
    }

    @Override
    public void deleteUser(UUID id) {
        log.info("Soft deleting user with id: {}", id);

        User user = findActiveById(id);
        user.setDeleted(true);
        user.setDeletedAt(Instant.now());
        user.setDeletedBy(getCurrentUsername());

        userRepository.save(user);
        log.info("User soft deleted successfully with id: {}", id);
    }

    private User findActiveById(UUID id) {
        return userRepository.findByUserIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    private String getCurrentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return "system";
        return auth.getName();
    }
}