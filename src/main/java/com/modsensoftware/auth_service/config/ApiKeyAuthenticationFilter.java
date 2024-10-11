package com.modsensoftware.auth_service.config;

import com.modsensoftware.auth_service.authorities.Role;
import com.modsensoftware.auth_service.models.ApiKeyAuthentication;
import com.modsensoftware.auth_service.utils.HttpRequestUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    @Value("${application.security.this-service-secret-api-key}")
    private String thisServiceSecretApiKey;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String apiKey = HttpRequestUtils.extractSecretKey(request);
        if (apiKey == null) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!apiKey.equals(thisServiceSecretApiKey)) {
            throw new BadCredentialsException("Invalid API KEY. API KEY doesn't match to this service secret key");
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(
                    new ApiKeyAuthentication(
                            apiKey,
                            Role.SECRET_KEY.getAuthorities()
                    )
            );
        }

        filterChain.doFilter(request, response);
    }
}