package com.backend.Seeder;

import com.backend.Entity.Employee;
import com.backend.Entity.Role;
import com.backend.Enum.AccountStatus;
import com.backend.Enum.RoleType;
import com.backend.Repository.RoleRepository;
import com.backend.Repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> adminUsers = Map.of(
            "admin@admin.gmail.com", "admin123"
    );

    private void seedRoles() {
        Arrays.stream(RoleType.values())
                .forEach(roleType -> {
                    if (!roleRepository.existsByName(roleType)) {
                        Role role = Role.builder()
                                .name(roleType)
                                .build();

                        roleRepository.save(role);
                    }
                });
    }

    private void seedAdmin() {

        Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                .orElseThrow();

        adminUsers.forEach((email, password) -> {

            if (!userRepository.existsByEmail(email)) {

                Employee admin = Employee.builder()
                        .firstName("Admin")
                        .lastName("System")
                        .companyName("Admin")
                        .jobTitle("Admin")
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .status(AccountStatus.ACTIVE)
                        .roles(Set.of(adminRole))
                        .build();

                userRepository.save(admin);
            }
        });
    }

    @Override
    public void run(@NonNull ApplicationArguments args) {
        seedRoles();
        seedAdmin();
    }
}