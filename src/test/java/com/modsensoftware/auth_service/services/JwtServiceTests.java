package com.modsensoftware.auth_service.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtServiceTests {

    private static JwtService jwtService;

    private static String JWT_TOKEN;

    private final List<String> authoritiesList = List.of("ROLE_USER", "ROLE_ADMIN");

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();

        String secretKey = "4Ai+5X3ZuK4J+T44sp0RDL48WeieeIq0boPRJb89MMU=";
        long expiration = 86400000L;

        try {
            Field secretKeyField = JwtService.class.getDeclaredField("secretKey");
            secretKeyField.setAccessible(true);
            secretKeyField.set(jwtService, secretKey);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authoritiesList);

        SecretKey signInKey = invokeGetSignInKey(jwtService);

        JWT_TOKEN = Jwts.builder()
                .setClaims(claims)
                .setSubject("testUser")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signInKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private static SecretKey invokeGetSignInKey(JwtService jwtService) {
        try {
            Method method = JwtService.class.getDeclaredMethod("getSignInKey");
            method.setAccessible(true);
            return (SecretKey) method.invoke(jwtService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke getSignInKey", e);
        }
    }

    @Test
    public void testExtractAllClaims() {
        Claims claims = jwtService.extractAllClaims(JWT_TOKEN);
        System.out.println(claims);
        assertNotNull(claims);
        assertEquals("testUser", claims.getSubject());
        assertEquals(List.of("ROLE_USER", "ROLE_ADMIN"), claims.get("authorities", List.class));
    }

    @Test
    public void testExtractSubject() {
        Claims claims = jwtService.extractAllClaims(JWT_TOKEN);
        String subject = jwtService.extractSubject(claims);
        assertNotNull(subject);
        assertEquals("testUser", subject);
    }

    @Test
    public void testExtractAuthorities() {
        Claims claims = jwtService.extractAllClaims(JWT_TOKEN);
        Collection<? extends GrantedAuthority> authorities = jwtService.extractAuthorities(claims);

        assertNotNull(authorities);

        assertEquals(authoritiesList.stream().map(SimpleGrantedAuthority::new).toList(), authorities);
    }

}
