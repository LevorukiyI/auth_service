package com.modsensoftware.auth_service.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ApiKeyAuthenticationTests {

    private ApiKeyAuthentication apiKeyAuthentication;
    private final String apiKey = "testApiKey";

    @BeforeEach
    public void setUp() {
        Collection<GrantedAuthority> authorities = Collections.emptyList();
        apiKeyAuthentication = new ApiKeyAuthentication(apiKey, authorities);
    }

    @Test
    public void testConstructor() {
        assertEquals(apiKey, apiKeyAuthentication.getPrincipal());
        assertTrue(apiKeyAuthentication.isAuthenticated());
    }

    @Test
    public void testGetCredentials() {
        assertNull(apiKeyAuthentication.getCredentials());
    }

    @Test
    public void testGetPrincipal() {
        assertEquals(apiKey, apiKeyAuthentication.getPrincipal());
    }

    @Test
    public void testGetAuthorities() {
        assertEquals(0, apiKeyAuthentication.getAuthorities().size());
    }
}