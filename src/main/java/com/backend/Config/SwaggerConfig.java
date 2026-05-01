package com.backend.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.servlet.context-path:/api/v1}")
    private String contextPath;

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(buildInfo())
                .servers(List.of(new Server().url(contextPath).description("Default Server")))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, buildSecurityScheme()))
                .security(List.of(new SecurityRequirement().addList(SECURITY_SCHEME_NAME)));
    }

    private Info buildInfo() {
        return new Info()
                .title("Expense Tracker API")
                .version("1.0.0")
                .description("API documentation for Expense Tracker")
                .contact(new Contact()
                        .name("Bibek Sah")
                        .email("bivek958@gmail.com"));
    }

    private SecurityScheme buildSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}