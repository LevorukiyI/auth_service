package com.modsensoftware.auth_service.config;

import com.modsensoftware.auth_service.authorities.Role;
import com.modsensoftware.auth_service.services.JwtService;
import com.modsensoftware.auth_service.utils.HttpRequestUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class JwtAuthenticationFilterTests {

    private JwtAuthenticationFilter filter;
    private JwtService jwtService;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        jwtService = Mockito.mock(JwtService.class);
        filter = new JwtAuthenticationFilter(jwtService);

        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        filterChain = Mockito.mock(FilterChain.class);


        securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testDoFilterInternalNullRequest() {
        request = null;
        assertThrows(NullPointerException.class, () -> {
            filter.doFilterInternal(request, response, filterChain);
        });
    }

    @Test
    public void testDoFilterInternalNullResponse() {
        response = null;
        assertThrows(NullPointerException.class, () -> {
            filter.doFilterInternal(request, response, filterChain);
        });
    }

    @Test
    public void testDoFilterInternalNullFilterChain() {
        filterChain = null;
        assertThrows(NullPointerException.class, () -> {
            filter.doFilterInternal(request, response, filterChain);
        });
    }

    @Test
    @SneakyThrows
    public void testDoFilterInternalWithNotNullAuthentication() {
        AbstractAuthenticationToken alreadyExistedAuthenticationToken = Mockito.mock(AbstractAuthenticationToken.class);
        when(securityContext.getAuthentication()).thenReturn(alreadyExistedAuthenticationToken);

        filter.doFilterInternal(request, response, filterChain);

        Mockito.verify(filterChain).doFilter(request, response);
        Mockito.verify(securityContext, Mockito.never()).setAuthentication(any());
    }

    @Test
    @SneakyThrows
    public void testDoFilterInternalWithNullJwtAccessToken() {
        try(MockedStatic<HttpRequestUtils> httpRequestUtilsMockedStatic = Mockito.mockStatic(HttpRequestUtils.class)) {
            httpRequestUtilsMockedStatic.when(() -> {
                HttpRequestUtils.extractAccessToken(request);
            }).thenReturn(null);

            filter.doFilterInternal(request, response, filterChain);
        }

            Mockito.verify(filterChain).doFilter(request, response);
            Mockito.verify(securityContext, Mockito.never()).setAuthentication(any());
    }

    @Test
    @SneakyThrows
    public void testDoFilterInternalWithNullUsername() {
        when(jwtService.extractSubject(any(Claims.class))).thenReturn(null);
        try(MockedStatic<HttpRequestUtils> httpRequestUtilsMockedStatic = Mockito.mockStatic(HttpRequestUtils.class)) {
            httpRequestUtilsMockedStatic.when(() -> {
                HttpRequestUtils.extractAccessToken(request);
            }).thenReturn("notNullAccessToken");
            filter.doFilterInternal(request, response, filterChain);
        }
        Mockito.verify(filterChain).doFilter(request, response);
        Mockito.verify(securityContext, Mockito.never()).setAuthentication(any());
    }

    @Test
    @SneakyThrows
    public void testDoFilterInternalWithValidJwtAndNullAuthentication() {
        when(securityContext.getAuthentication()).thenReturn(null);
        when(jwtService.extractSubject((Claims) any())).thenReturn("username");
        Collection<GrantedAuthority> grantedAuthorities = Role.USER.getAuthorities().stream()
                .map(simpleGrantedAuthority -> (GrantedAuthority)simpleGrantedAuthority)
                .toList();
        doReturn(grantedAuthorities).when(jwtService).extractAuthorities(any());

        try(MockedStatic<HttpRequestUtils> httpRequestUtilsMockedStatic = Mockito.mockStatic(HttpRequestUtils.class)) {
            httpRequestUtilsMockedStatic.when(() -> {
                HttpRequestUtils.extractAccessToken(request);
            }).thenReturn("notNullAccessToken");
        filter.doFilterInternal(request, response, filterChain);
        }

        Mockito.verify(filterChain).doFilter(request, response);
        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        Mockito.verify(securityContext).setAuthentication(authCaptor.capture());

        UsernamePasswordAuthenticationToken capturedAuth = authCaptor.getValue();
        assertEquals("username", capturedAuth.getPrincipal());
        assertEquals(
                grantedAuthorities,
                capturedAuth.getAuthorities()
        );
    }

}
