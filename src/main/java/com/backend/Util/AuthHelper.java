package com.backend.Util;

import com.backend.Exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class AuthHelper {

    public String getCurrentEmail() {
        return getAuthentication().getName();
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    public boolean hasRole(String role) {
        return getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    public boolean hasAnyRole(String... roles) {
        Collection<? extends GrantedAuthority> authorities = getAuthorities();
        for (String role : roles) {
            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role))) {
                return true;
            }
        }
        return false;
    }

    public List<String> getCurrentRoles() {
        return getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    public Object getCurrentPrincipal() {
        return getAuthentication().getPrincipal();
    }

    public boolean isCurrentUser(String email) {
        return getCurrentEmail().equalsIgnoreCase(email);
    }

    public void requireRole(String role) {
        if (!hasRole(role)) {
            log.warn("Access denied — required role: ROLE_{}", role);
            throw new SecurityException("Access denied: required role ROLE_" + role);
        }
    }

    public void requireAnyRole(String... roles) {
        if (!hasAnyRole(roles)) {
            log.warn("Access denied — required one of roles: {}", (Object) roles);
            throw new SecurityException("Access denied: insufficient role");
        }
    }

    private Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("No authenticated user found in SecurityContext");
            throw new ResourceNotFoundException("No authenticated user found");
        }
        return authentication;
    }

    private Collection<? extends GrantedAuthority> getAuthorities() {
        try {
            return getAuthentication().getAuthorities();
        } catch (Exception e) {
            log.warn("Could not retrieve authorities: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}