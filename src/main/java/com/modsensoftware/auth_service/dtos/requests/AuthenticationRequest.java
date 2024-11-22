package com.modsensoftware.auth_service.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to authenticate user")
public record AuthenticationRequest (
    @Schema(description = "name of user, which uses as subject in library service",
            example = "name")
    @NotBlank(message = "username can't be blank")
    String username,
    @Schema(description = "password of user",
            example = "password")
    @NotBlank(message = "password can't be blank")
    String password
){
}
