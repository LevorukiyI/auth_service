package com.modsensoftware.auth_service.auditing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationAuditAwareTests {

    private ApplicationAuditAware auditorAware;
    private SecurityContext context;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        auditorAware = new ApplicationAuditAware();
        context = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
    }

    @Test
    public void testGetCurrentAuditorNoAuthentication() {
        Mockito.when(context.getAuthentication()).thenReturn(null);

        Optional<String> auditor = auditorAware.getCurrentAuditor();
        assertEquals(Optional.empty(), auditor);
    }

    @Test
    public void testGetCurrentAuditorAnonymousUser() {
        Authentication authentication = new AnonymousAuthenticationToken("key", "anonymousUser",
                Collections.singletonList(() -> "ROLE_ANONYMOUS"));

        Mockito.when(context.getAuthentication()).thenReturn(authentication);

        Optional<String> auditor = auditorAware.getCurrentAuditor();
        assertEquals(Optional.empty(), auditor);
    }

    @Test
    public void testGetCurrentAuditorNullPrincipal() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, "password");
        Mockito.when(context.getAuthentication()).thenReturn(authentication);

        Optional<String> auditor = auditorAware.getCurrentAuditor();
        assertEquals(Optional.empty(), auditor);
    }

    @Test
    public void testGetCurrentAuditorAuthenticatedUser() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("key", "anonymousUser",
                Collections.singletonList(() -> "ROLE_ANONYMOUS"));
        Mockito.when(context.getAuthentication()).thenReturn(authentication);

        Optional<String> auditor = auditorAware.getCurrentAuditor();
        assertEquals(Optional.of("key"), auditor);
    }
}
