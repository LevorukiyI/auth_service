package com.modsensoftware.auth_service.config;

import com.modsensoftware.auth_service.services.JwtService;
import com.modsensoftware.auth_service.utils.HttpRequestUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if(SecurityContextHolder.getContext().getAuthentication() != null){
            filterChain.doFilter(request, response);
            return;
        }
        String jwtAccessToken = HttpRequestUtils.extractAccessToken(request);
        if(jwtAccessToken == null){
            filterChain.doFilter(request, response);
            return;
        }

        Claims accessTokenClaims = jwtService.extractAllClaims(jwtAccessToken);
        final String username = jwtService.extractSubject(accessTokenClaims);
        if(username == null){
            filterChain.doFilter(request, response);
            return;
        }
        Collection<? extends GrantedAuthority> authorities = jwtService.extractAuthorities(accessTokenClaims);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                username,
                null,
                authorities
        );
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}