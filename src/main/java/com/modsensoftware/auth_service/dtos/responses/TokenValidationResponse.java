package com.modsensoftware.auth_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "response, that provides to confirm, that token is valid")
public record TokenValidationResponse (
        @Schema(description = "boolean temp, that shows is jwt token valid or not",
                example = "name")
        @NotNull(message = "isAccessTokenValid can't be null")
        boolean isAccessTokenValid
) {
}
