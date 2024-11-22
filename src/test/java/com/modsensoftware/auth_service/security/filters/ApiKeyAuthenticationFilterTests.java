package com.modsensoftware.auth_service.security.filters;

import com.modsensoftware.auth_service.models.authorities.Role;
import com.modsensoftware.auth_service.models.ApiKeyAuthentication;
import com.modsensoftware.auth_service.security.utils.HttpRequestUtils;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ApiKeyAuthenticationFilterTests {

    private ApiKeyAuthenticationFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    private SecurityContext securityContext;
    private String thisServiceSecretApiKey;

    @BeforeEach
    public void setUp() {
        filter = new ApiKeyAuthenticationFilter();
        thisServiceSecretApiKey = "thisServiceCorrectApiKey";
        try {
            Field secretApiKeyField = ApiKeyAuthenticationFilter.class.getDeclaredField("thisServiceSecretApiKey");
            secretApiKeyField.setAccessible(true);
            secretApiKeyField.set(filter, thisServiceSecretApiKey);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

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
    public void testDoFilterInternalNulFilterChain() {
        filterChain = null;
        assertThrows(NullPointerException.class, () -> {
            filter.doFilterInternal(request, response, filterChain);
        });
    }

    @Test
    @SneakyThrows
    public void testDoFilterInternalNullApiKey() {
        try (MockedStatic<HttpRequestUtils> httpRequestUtilsMockedStatic = Mockito.mockStatic(HttpRequestUtils.class)) {
            httpRequestUtilsMockedStatic.when(() -> {
                HttpRequestUtils.extractSecretKey(request);
            }).thenReturn(null);
            filter.doFilterInternal(request, response, filterChain);
        }

        Mockito.verify(filterChain).doFilter(request, response);
        Mockito.verify(securityContext, Mockito.never()).setAuthentication(any());
    }

    @Test
    @SneakyThrows
    public void testDoFilterInternalWithInvalidApiKey() {
        String invalidationString = "if you add this string to the ApiKey"
                + "the ApiKey will no longer be valid."
                + "Which is logical, because this is a garbage line";
        String invalidApiKey = thisServiceSecretApiKey + invalidationString;
        try (MockedStatic<HttpRequestUtils> httpRequestUtilsMockedStatic = Mockito.mockStatic(HttpRequestUtils.class)) {
            httpRequestUtilsMockedStatic.when(() -> {
                HttpRequestUtils.extractSecretKey(request);
            }).thenReturn(invalidApiKey);
            assertThrows(BadCredentialsException.class, () -> {
                filter.doFilterInternal(request, response, filterChain);
            });
        }
    }

    @Test
    @SneakyThrows
    public void testDoFilterInternalWithValidApiKeyAndNullAuthentication() {
        when(securityContext.getAuthentication()).thenReturn(null);
        try (MockedStatic<HttpRequestUtils> httpRequestUtilsMockedStatic = Mockito.mockStatic(HttpRequestUtils.class)) {
            httpRequestUtilsMockedStatic.when(() -> {
                HttpRequestUtils.extractSecretKey(request);
            }).thenReturn(thisServiceSecretApiKey);

            filter.doFilterInternal(request, response, filterChain);
        }

        ArgumentCaptor<ApiKeyAuthentication> authCaptor = ArgumentCaptor.forClass(ApiKeyAuthentication.class);
        Mockito.verify(securityContext).setAuthentication(authCaptor.capture());

        ApiKeyAuthentication capturedAuth = authCaptor.getValue();
        assertEquals(thisServiceSecretApiKey, capturedAuth.getPrincipal());
        assertEquals(Role.SECRET_KEY.getAuthorities(), capturedAuth.getAuthorities());
    }

    @Test
    @SneakyThrows
    public void testDoFilterInternalCallsFilterChain() {
        when(securityContext.getAuthentication()).thenReturn(null);
        try (MockedStatic<HttpRequestUtils> httpRequestUtilsMockedStatic = Mockito.mockStatic(HttpRequestUtils.class)) {
            httpRequestUtilsMockedStatic.when(() -> {
                HttpRequestUtils.extractSecretKey(request);
            }).thenReturn(thisServiceSecretApiKey);

            filter.doFilterInternal(request, response, filterChain);
        }

        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    @SneakyThrows
    public void testDoFilterInternalWithNotNullAuthentication() {
        AbstractAuthenticationToken alreadyExistedAuthenticationToken = Mockito.mock(AbstractAuthenticationToken.class);
        when(securityContext.getAuthentication()).thenReturn(alreadyExistedAuthenticationToken);
        try (MockedStatic<HttpRequestUtils> httpRequestUtilsMockedStatic = Mockito.mockStatic(HttpRequestUtils.class)) {
            httpRequestUtilsMockedStatic.when(() -> {
                HttpRequestUtils.extractSecretKey(request);
            }).thenReturn(thisServiceSecretApiKey);

            filter.doFilterInternal(request, response, filterChain);
        }

        Mockito.verify(filterChain).doFilter(request, response);
        Mockito.verify(securityContext, Mockito.never()).setAuthentication(any());
    }


}
