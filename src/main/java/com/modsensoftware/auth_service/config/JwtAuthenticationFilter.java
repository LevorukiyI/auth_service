package com.modsensoftware.auth_service.config;

import com.modsensoftware.auth_service.models.JwtTokenType;
import com.modsensoftware.auth_service.models.User;
import com.modsensoftware.auth_service.services.JwtService;
import com.modsensoftware.auth_service.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if(Arrays.stream(SecurityConfiguration.WHITE_LIST_URL).anyMatch(url -> request.getServletPath().contains(url))){
            filterChain.doFilter(request, response);
            return;
        }
        String jwtAccessToken = jwtService.extractAccessToken(request);
        if(jwtAccessToken == null){
            filterChain.doFilter(request, response);
            return;
        }

        final String username = jwtService.extractSubject(jwtAccessToken);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = this.userService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwtAccessToken, user, JwtTokenType.ACCESS)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        user.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
