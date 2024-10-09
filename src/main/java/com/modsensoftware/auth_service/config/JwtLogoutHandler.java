package com.modsensoftware.auth_service.config;

import com.modsensoftware.auth_service.exceptions.UserNotFoundException;
import com.modsensoftware.auth_service.models.User;
import com.modsensoftware.auth_service.repositories.JwtRefreshTokenRepository;
import com.modsensoftware.auth_service.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {
    private final UserRepository userRepository;
    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        User user = userRepository.findByUsername((String) authentication.getPrincipal())
                .orElseThrow(()-> new UserNotFoundException("User with such email not exists"));
        jwtRefreshTokenRepository.deleteIfExistsByUser(user);
    }
}
