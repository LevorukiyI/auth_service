package com.modsensoftware.auth_service.config;

import com.modsensoftware.auth_service.auditing.ApplicationAuditAware;
import com.modsensoftware.auth_service.repositories.JwtRefreshTokenRepository;
import com.modsensoftware.auth_service.repositories.UserRepository;
import com.modsensoftware.auth_service.services.DefaultUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class ApplicationConfig {

    private final DefaultUserDetailsService defaultUserDetailsService;
    private final UserRepository userRepository;
    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

    @Bean
    public JwtLogoutHandler jwtLogoutHandler() {
        return new JwtLogoutHandler(userRepository, jwtRefreshTokenRepository);
    }

    @Bean
    public AuditorAware<Long> auditorAware() {
        return new ApplicationAuditAware();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider(passwordEncoder());
        daoAuthenticationProvider.setForcePrincipalAsString(true);
        daoAuthenticationProvider.setUserDetailsService(defaultUserDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}

