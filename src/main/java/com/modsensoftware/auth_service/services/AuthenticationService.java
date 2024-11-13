package com.modsensoftware.auth_service.services;

import com.modsensoftware.auth_service.clients.LibraryServiceClient;
import com.modsensoftware.auth_service.exceptions.UserAlreadyExistsException;
import com.modsensoftware.auth_service.exceptions.UserNotFoundException;
import com.modsensoftware.auth_service.models.JwtRefreshToken;
import com.modsensoftware.auth_service.models.User;
import com.modsensoftware.auth_service.repositories.JwtRefreshTokenRepository;
import com.modsensoftware.auth_service.repositories.UserRepository;
import com.modsensoftware.auth_service.requests.AuthenticationRequest;
import com.modsensoftware.auth_service.requests.RefreshAccessTokenRequest;
import com.modsensoftware.auth_service.requests.RegisterRequest;
import com.modsensoftware.auth_service.responses.AuthenticationResponse;
import com.modsensoftware.auth_service.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final LibraryServiceClient libraryServiceClient;

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByUsername((String) authentication.getPrincipal())
                .orElseThrow(()-> new UserNotFoundException("User with such email not exists"));
        var jwtToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeUserJwtRefreshToken(user);
        saveUserRefreshToken(user, refreshToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthenticationResponse register(RegisterRequest registerRequest){
        if(userRepository.existsByUsername(registerRequest.getUsername())){
            throw new UserAlreadyExistsException("user with such username already exists");
        }
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(encodedPassword)
        .build();
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        userRepository.save(user);
        saveUserRefreshToken(user, refreshToken);

        libraryServiceClient.registerUser(Mapper.from(user));

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void revokeUserJwtRefreshToken(User user){
        jwtRefreshTokenRepository.deleteIfExistsByUser(user);
    }

    private void saveUserRefreshToken(User user, String jwtToken) {
        var token = JwtRefreshToken.builder()
                .user(user)
                .token(jwtToken)
                .build();
        jwtRefreshTokenRepository.save(token);
    }

    public AuthenticationResponse refreshAccessToken(
            RefreshAccessTokenRequest refreshAccessTokenRequest
    ){
        final String refreshToken = refreshAccessTokenRequest.getJwtRefreshToken();
        if(refreshToken == null){
            throw new IllegalArgumentException("Invalid header Authorization");
        }
        final String username = jwtService.extractSubject(refreshToken);
        if (username == null) {
            throw new IllegalArgumentException("Invalid username in refreshToken, username can't be null");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("token contains invalid username, there is no user with such username"));

        String accessToken = jwtService.generateAccessToken(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
