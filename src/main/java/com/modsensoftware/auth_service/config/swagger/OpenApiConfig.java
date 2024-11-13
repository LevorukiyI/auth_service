package com.modsensoftware.auth_service.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("auth-service-api")
                        .version("1.0")
                        .description("documentation for auth_service api"))
                .paths(addCustomPaths());
    }

    private Paths addCustomPaths() {
        Paths paths = new Paths();

        PathItem logoutPath = new PathItem()
                .post(new Operation()
                        .summary("Logout user")
                        .description("Logs out a user.")
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse().description("Successfully logged out"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized, invalid token"))
                                .addApiResponse("404", new ApiResponse().description("User not found"))));


        paths.addPathItem("/auth/logout", logoutPath);
        return paths;
    }
}