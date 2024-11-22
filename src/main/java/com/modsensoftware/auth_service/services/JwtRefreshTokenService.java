package com.modsensoftware.auth_service.services;

import com.modsensoftware.auth_service.models.JwtRefreshToken;
import com.modsensoftware.auth_service.models.User;
import com.modsensoftware.auth_service.repositories.JwtRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JwtRefreshTokenService {
    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeUserJwtRefreshToken(User user){
        jwtRefreshTokenRepository.deleteIfExistsByUser(user);
    }

    @Transactional
    public void saveUserRefreshToken(User user, String jwtToken) {
        var token = JwtRefreshToken.builder()
                .user(user)
                .token(jwtToken)
                .build();
        jwtRefreshTokenRepository.save(token);
    }
}
