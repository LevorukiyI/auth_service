package com.modsensoftware.auth_service.services;

import com.modsensoftware.auth_service.clients.LibraryServiceClient;
import com.modsensoftware.auth_service.exceptions.*;
import com.modsensoftware.auth_service.models.User;
import com.modsensoftware.auth_service.repositories.JwtRefreshTokenRepository;
import com.modsensoftware.auth_service.repositories.UserRepository;
import com.modsensoftware.auth_service.dtos.requests.AuthenticationRequest;
import com.modsensoftware.auth_service.dtos.requests.RefreshAccessTokenRequest;
import com.modsensoftware.auth_service.dtos.requests.RegisterRequest;
import com.modsensoftware.auth_service.dtos.responses.AuthenticationResponse;
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
    private final JwtRefreshTokenService jwtRefreshTokenService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final LibraryServiceClient libraryServiceClient;
    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        User user = userRepository.findByUsername((String) authentication.getPrincipal())
                .orElseThrow(()-> new UserNotFoundException("User with such email not exists"));
        String jwtToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        jwtRefreshTokenService.revokeUserJwtRefreshToken(user);
        jwtRefreshTokenService.saveUserRefreshToken(user, refreshToken);
        return new AuthenticationResponse(
                jwtToken,
                refreshToken
        );
    }

    @Transactional
    public AuthenticationResponse register(RegisterRequest registerRequest){
        if(userRepository.existsByUsername(registerRequest.username())){
            throw new UserAlreadyExistsException("user with such username already exists");
        }
        String encodedPassword = passwordEncoder.encode(registerRequest.password());
        User user = User.builder()
                .username(registerRequest.username())
                .password(encodedPassword)
        .build();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        userRepository.save(user);
        jwtRefreshTokenService.saveUserRefreshToken(user, refreshToken);

        libraryServiceClient.registerUser(Mapper.from(user));

        return new AuthenticationResponse(
                accessToken,
                refreshToken
        );
    }

    public AuthenticationResponse refreshAccessToken(
            RefreshAccessTokenRequest refreshAccessTokenRequest
    ){
        final String refreshToken = refreshAccessTokenRequest.jwtRefreshToken();
        if(refreshToken == null){
            throw new JwtRefreshTokenNotProvidedException();
        }
        final String username = jwtService.extractSubject(refreshToken);
        if (username == null) {
            throw new InvalidUsernameInJwtRefreshToken();
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("token contains invalid username, there is no user with such username"));

        String accessToken = jwtService.generateAccessToken(user);

        return new AuthenticationResponse(
                accessToken,
                refreshToken
        );
    }

    @Transactional
    public void logout(Authentication authentication){
        if(authentication == null){
            throw new LogoutUserAuthenticationNotProvided();
        }
        String principal = (String) authentication.getPrincipal();
        User user = userRepository.findByUsername(principal)
                .orElseThrow(()-> new UserNotFoundException("User with such email not exists"));
        jwtRefreshTokenRepository.deleteIfExistsByUser(user);
    }
}