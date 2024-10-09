package com.modsensoftware.auth_service.controllers;

import com.modsensoftware.auth_service.requests.AuthenticationRequest;
import com.modsensoftware.auth_service.requests.RefreshAccessTokenRequest;
import com.modsensoftware.auth_service.requests.RegisterRequest;
import com.modsensoftware.auth_service.responses.AuthenticationResponse;
import com.modsensoftware.auth_service.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;


    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate a user",
            description = "Returns an authentication response containing access and refresh tokens.")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Parameter(description = "Authentication request containing username and password",
                    required = true)
            @RequestBody AuthenticationRequest authenticationRequest) {
        AuthenticationResponse authenticationResponse =
                authenticationService.authenticate(authenticationRequest);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user",
            description = "Returns an authentication response containing access and refresh tokens.")
    public ResponseEntity<AuthenticationResponse> register(
            @Parameter(description = "Registration request containing user details",
                    required = true)
            @RequestBody RegisterRequest registerRequest) {
        AuthenticationResponse authenticationResponse =
                authenticationService.register(registerRequest);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token",
            description = "Returns a new authentication response.")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @Parameter(description = "Request containing the refresh token",
                    required = true)
            @RequestBody RefreshAccessTokenRequest refreshAccessTokenRequest){
        AuthenticationResponse authenticationResponse =
                authenticationService.refreshAccessToken(refreshAccessTokenRequest);
        return ResponseEntity.ok(authenticationResponse);
    }
}
