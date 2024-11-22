package com.modsensoftware.auth_service;

import com.modsensoftware.auth_service.annotations.ContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ContainerTest
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
