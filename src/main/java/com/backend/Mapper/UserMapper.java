package com.backend.Mapper;

import com.backend.Dto.Response.UserResponse;
import com.backend.Entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getProfilePictureUrl(),
                user.getBio(),
                user.getStatus(),
                mapRoles(user),
                user.getCreatedAt(),
                user.getCreatedBy(),
                user.getUpdatedAt(),
                user.getUpdatedBy()
        );
    }

    private Set<String> mapRoles(User user) {
        if (user.getRoles() == null) return Set.of();
        return user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}