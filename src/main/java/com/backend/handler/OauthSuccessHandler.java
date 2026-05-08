package com.backend.handler;

import com.backend.Entity.Employee;
import com.backend.Entity.JobSeeker;
import com.backend.Entity.User;
import com.backend.Enum.AccountStatus;
import com.backend.Enum.RoleType;
import com.backend.Repository.RoleRepository;
import com.backend.Repository.UserRepository;
import com.backend.Services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class OauthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authentication type");
            return;
        }

        // Retrieve the role choice saved in session from the initial click
        String targetRole = (String) request.getSession().getAttribute("OAUTH_REGISTRATION_ROLE");
        request.getSession().removeAttribute("OAUTH_REGISTRATION_ROLE");

        OAuth2User oAuth2User = oauthToken.getPrincipal();
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        log.info("OAuth2 login via provider: {}", registrationId);

        User user = switch (registrationId) {
            case "google" -> handleGoogle(oAuth2User, targetRole);
            case "github" -> handleGithub(oAuth2User, targetRole);
            case "facebook" -> handleFacebook(oAuth2User, targetRole);
            case "apple" -> handleApple(oAuth2User, targetRole);
            case "microsoft" -> handleMicrosoft(oAuth2User, targetRole);
            case "linkedin" -> handleLinkedIn(oAuth2User, targetRole);
            default -> throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
        };

        Authentication userAuth = toAuthentication(user);
        String accessToken = jwtService.generateAccessToken(userAuth);
        String refreshToken = jwtService.generateRefreshToken(userAuth);

        String body = """
                {
                  "accessToken":  "%s",
                  "refreshToken": "%s"
                }
                """.formatted(accessToken, refreshToken);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(body);
    }

    private User handleGoogle(OAuth2User oAuth2User, String role) {
        return findOrCreate(oAuth2User.getAttribute("email"), oAuth2User.getAttribute("name"), "google", oAuth2User.getAttribute("sub"), role);
    }

    private User handleGithub(OAuth2User oAuth2User, String role) {
        String email = Optional.ofNullable((String) oAuth2User.getAttribute("email"))
                .orElseGet(() -> oAuth2User.getAttribute("login") + "@github.noemail");
        return findOrCreate(email, oAuth2User.getAttribute("name"), "github", String.valueOf(oAuth2User.getAttribute("id")), role);
    }

    private User handleFacebook(OAuth2User oAuth2User, String role) {
        return findOrCreate(oAuth2User.getAttribute("email"), oAuth2User.getAttribute("name"), "facebook", oAuth2User.getAttribute("id"), role);
    }

    private User handleApple(OAuth2User oAuth2User, String role) {
        return findOrCreate(oAuth2User.getAttribute("email"), oAuth2User.getAttribute("name"), "apple", oAuth2User.getAttribute("sub"), role);
    }

    private User handleMicrosoft(OAuth2User oAuth2User, String role) {
        return findOrCreate(oAuth2User.getAttribute("email"), oAuth2User.getAttribute("displayName"), "microsoft", oAuth2User.getAttribute("id"), role);
    }

    private User handleLinkedIn(OAuth2User oAuth2User, String role) {
        String name = "%s %s".formatted(oAuth2User.getAttribute("localizedFirstName"), oAuth2User.getAttribute("localizedLastName")).strip();
        return findOrCreate(oAuth2User.getAttribute("email"), name, "linkedin", oAuth2User.getAttribute("id"), role);
    }

    private User findOrCreate(String email, String fullName, String providerName, String providerId, String userType) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    var defaultRole = roleRepository.findByName(RoleType.JOBSEEKER)
                            .orElseThrow(() -> new IllegalStateException("Default role not found: " + RoleType.JOBSEEKER.name()));

                    String firstName = "";
                    String lastName = "";
                    if (fullName != null && !fullName.isBlank()) {
                        String[] parts = fullName.split("\\s+", 2);
                        firstName = parts[0];
                        lastName = parts.length > 1 ? parts[1] : "";
                    }

                    User newUser;
                    // Logic to instantiate the correct CONCRETE subclass
                    if ("EMPLOYEE".equalsIgnoreCase(userType)) {
                        newUser = Employee.builder()
                                .email(email)
                                .firstName(firstName)
                                .lastName(lastName)
                                .providerName(providerName)
                                .providerId(providerId)
                                .status(AccountStatus.ACTIVE)
                                .password(UUID.randomUUID().toString())
                                .roles(Set.of(defaultRole))
                                .companyName("Pending Setup") // Must fill non-null fields
                                .jobTitle("Pending Setup")
                                .build();
                    } else {
                        newUser = JobSeeker.builder()
                                .email(email)
                                .firstName(firstName)
                                .lastName(lastName)
                                .providerName(providerName)
                                .providerId(providerId)
                                .status(AccountStatus.ACTIVE)
                                .password(UUID.randomUUID().toString())
                                .roles(Set.of(defaultRole))
                                .build();
                    }

                    log.info("Registering new {} via OAuth2: {}", newUser.getClass().getSimpleName(), email);
                    return userRepository.save(newUser);
                });
    }

    private Authentication toAuthentication(User user) {
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getName().name()))
                .toList();
        return new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
    }
}