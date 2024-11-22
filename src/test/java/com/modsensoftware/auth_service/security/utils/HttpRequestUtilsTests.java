package com.modsensoftware.auth_service.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class HttpRequestUtilsTests {

    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    public void testExtractSecretKey() {
        String expectedApiKey = "my-secret-key";
        Mockito.when(request.getHeader("x-api-key")).thenReturn(expectedApiKey);

        String actualApiKey = HttpRequestUtils.extractSecretKey(request);

        assertEquals(expectedApiKey, actualApiKey);
    }

    @Test
    public void testExtractSecretKeyNullRequest() {
        assertThrows(NullPointerException.class,
                () -> HttpRequestUtils.extractSecretKey(null));
    }

    @Test
    public void testExtractAccessTokenWithBearer() {
        String authorizationHeader = "Bearer my-access-token";
        Mockito.when(request.getHeader("Authorization")).thenReturn(authorizationHeader);

        String actualToken = HttpRequestUtils.extractAccessToken(request);

        assertEquals("my-access-token", actualToken);
    }

    @Test
    public void testExtractAccessTokenWithoutBearer() {
        String authorizationHeader = "Basic some-credentials";
        Mockito.when(request.getHeader("Authorization")).thenReturn(authorizationHeader);

        String actualToken = HttpRequestUtils.extractAccessToken(request);

        assertNull(actualToken);
    }

    @Test
    public void testExtractAccessTokenWithNullHeader() {
        Mockito.when(request.getHeader("Authorization")).thenReturn(null);

        String actualToken = HttpRequestUtils.extractAccessToken(request);

        assertNull(actualToken);
    }

    @Test
    public void testExtractAccessTokenWithEmptyAuthorization() {
        String actualToken = HttpRequestUtils.extractAccessToken("");

        assertNull(actualToken);
    }

    @Test
    public void testExtractAccessTokenWithInvalidBearer() {
        String actualToken = HttpRequestUtils.extractAccessToken("Bearer");

        assertNull(actualToken);
    }

    @Test
    public void testExtractAccessTokenWithMalformedBearer() {
        String malformedAuthorizationHeader = "Bearer ";
        String actualToken = HttpRequestUtils.extractAccessToken(malformedAuthorizationHeader);
        assertEquals("", actualToken);
    }

    @Test
    public void testExtractAccessTokenWithSpaces() {
        String authorizationHeader = "Bearer my-access-token";
        String actualToken = HttpRequestUtils.extractAccessToken(authorizationHeader.trim());
        assertEquals("my-access-token", actualToken);
    }

    @Test
    public void testExtractAccessTokenWithExtraSpaces() {
        String authorizationHeader = "    Bearer my-access-token   ";
        String actualToken = HttpRequestUtils.extractAccessToken(authorizationHeader.trim());
        assertEquals("my-access-token", actualToken);
    }

    @Test
    public void testExtractAccessTokenWithNullRequest() {
        assertThrows(NullPointerException.class,
                ()-> HttpRequestUtils.extractAccessToken((HttpServletRequest) null));
    }

    @Test
    public void testHttpRequestUtilsConstructor() {
        HttpRequestUtils utils = new HttpRequestUtils();
        assertNotNull(utils);
    }
}
