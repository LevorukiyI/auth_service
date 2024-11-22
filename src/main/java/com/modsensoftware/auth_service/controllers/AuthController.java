package com.modsensoftware.auth_service.controllers;

import com.modsensoftware.auth_service.dtos.requests.AuthenticationRequest;
import com.modsensoftware.auth_service.dtos.requests.RefreshAccessTokenRequest;
import com.modsensoftware.auth_service.dtos.requests.RegisterRequest;
import com.modsensoftware.auth_service.dtos.responses.AuthenticationResponse;
import com.modsensoftware.auth_service.exceptions.responses.ExceptionResponse;
import com.modsensoftware.auth_service.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Authenticate a user",
            description = "Returns an authentication response containing access and refresh tokens.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "user authenticated **successfully**",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "wrong password",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "user with such username not found <br>",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Authentication request containing username and password",
            content = @Content(schema = @Schema(implementation = AuthenticationRequest.class))
    )
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest authenticationRequest) {
        AuthenticationResponse authenticationResponse =
                authenticationService.authenticate(authenticationRequest);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user",
            description = "Returns an authentication response containing access and refresh tokens.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "user registered **successfully**",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "user with such username already exists <br>",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Registration request containing user details",
            content = @Content(schema = @Schema(implementation = RegisterRequest.class))
    )
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
        AuthenticationResponse authenticationResponse =
                authenticationService.register(registerRequest);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token",
            description = "Returns a new authentication response.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "token refreshed **successfully**",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "a specific error description will be passed to the ExceptionResponse. <br>"
                            + "jwt refresh token no provided in request"
                            + "**OR** Invalid username in refreshToken, username can't be null",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "token contains invalid username, there is no user with such username",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Request containing the refresh token",
            content = @Content(schema = @Schema(implementation = RefreshAccessTokenRequest.class))
    )
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @Valid @RequestBody RefreshAccessTokenRequest refreshAccessTokenRequest){
        AuthenticationResponse authenticationResponse =
                authenticationService.refreshAccessToken(refreshAccessTokenRequest);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user",
            description = "Logout user.",
            security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "logged out **successfully**",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "authentication where not provided or Bearer access_token doesn't contain authentication",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized, invalid token",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
    })
    public ResponseEntity<HttpStatus> logout(
            Authentication authentication
    ){
        authenticationService.logout(authentication);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleDataAccessException(BadCredentialsException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                "user provided bad credentials"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }
}
