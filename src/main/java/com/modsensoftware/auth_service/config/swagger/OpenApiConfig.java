package com.modsensoftware.auth_service.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "auth-service API", version = "1.0",
        description = "documentation for auth_service api"))
@SecurityScheme(name = "Bearer",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Use 'Bearer access_token' to authorize.")
@SecurityScheme(name = "x-api-key",
        type = SecuritySchemeType.HTTP,
        scheme = "x-api-key",
        in = SecuritySchemeIn.HEADER,
        paramName = "x-api-key",
        description = "Use 'x-api-key secret_api_key' to authorize.")
public class OpenApiConfig {
}