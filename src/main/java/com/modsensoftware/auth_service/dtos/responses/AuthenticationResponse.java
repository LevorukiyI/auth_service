package com.modsensoftware.auth_service.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "response, that provides to confirm, that user is authenticated")
public record AuthenticationResponse(
        @Schema(description = "An access token (from an authorization server) allows temporary ",
                example = "name")
        @NotBlank(message = "accessToken can't be blank")
        @JsonProperty("access_token")
        String accessToken,

        @Schema(description = "allow users to log in and stay connected without providing their passwords for long periods.",
                example = "name")
        @NotBlank(message = "refreshToken can't be blank")
        @JsonProperty("refresh_token")
        String refreshToken
) {
}
