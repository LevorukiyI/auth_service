package com.modsensoftware.auth_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.modsensoftware.auth_service.annotations.ContainerTest;
import com.modsensoftware.auth_service.config.ApplicationConfig;
import com.modsensoftware.auth_service.config.security.SecurityConfiguration;
import com.modsensoftware.auth_service.controllers.AuthController;
import com.modsensoftware.auth_service.dtos.requests.AuthenticationRequest;
import com.modsensoftware.auth_service.dtos.requests.RefreshAccessTokenRequest;
import com.modsensoftware.auth_service.dtos.requests.RegisterRequest;
import com.modsensoftware.auth_service.dtos.responses.AuthenticationResponse;
import com.modsensoftware.auth_service.models.User;
import com.modsensoftware.auth_service.repositories.JwtRefreshTokenRepository;
import com.modsensoftware.auth_service.repositories.UserRepository;
import com.modsensoftware.auth_service.security.filters.ApiKeyAuthenticationFilter;
import com.modsensoftware.auth_service.services.AuthenticationService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({SecurityConfiguration.class, ApplicationConfig.class})
@ActiveProfiles("test")
@ContainerTest
@WireMockTest(httpPort = 8082)
public class AuthControllerTest {

    @Autowired
    private JwtRefreshTokenRepository jwtRefreshTokenRepository;
    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Autowired
    private ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    @Autowired
    private AuthenticationService authenticationService;

    private static final int WIREMOCK_PORT = 8082;

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        jwtRefreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authenticationService))
                .addFilters(apiKeyAuthenticationFilter)
                .build();

        wireMockServer = new WireMockServer();
        WireMock.configureFor("localhost", WIREMOCK_PORT);
        wireMockServer.start();

        stubFor(WireMock.post(urlEqualTo("/users/register-user"))
                .withHeader("x-api-key", equalTo("ahgNSFHEKdbmrVvmKV2GmDGRTOzVAjsJD8k7crjR5yM="))
                .willReturn(aResponse()
                        .withStatus(200)));
    }

    @AfterEach
    public void tearDown() {
        jwtRefreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        wireMockServer.stop();
    }
    @Test
    public void testRegistrationSingleUserWithValidData() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user", "password");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());
    }

    @Test
    public void testRegistrationTwoSimilarUsersWithValidData() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user", "password");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testRegistrationTwoDifferentUsersWithValidData() throws Exception {
        RegisterRequest registerRequest1 = new RegisterRequest("user1", "password");
        RegisterRequest registerRequest2 = new RegisterRequest("user2", "password");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());
    }

    @Test
    public void testOneUserRegistrationAndAuthentication() throws Exception {
        User user = User.builder()
                .username("user3")
                .password("password3")
                .build();

        RegisterRequest registerRequest = new RegisterRequest(user.getUsername(), user.getPassword());
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());

        AuthenticationRequest authRequest = new AuthenticationRequest(user.getUsername(), user.getPassword());
        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());
    }

    @Test
    public void testOneUserRegistrationAndOtherAuthentication() throws Exception {
        User user1 = User.builder()
                .username("user1")
                .password("password1")
                .build();
        User user2 = User.builder()
                .username("user2")
                .password("password2")
                .build();

        RegisterRequest registerRequest = new RegisterRequest(user1.getUsername(), user1.getPassword());
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());

        AuthenticationRequest authRequest = new AuthenticationRequest(user2.getUsername(), user2.getPassword());
        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void registerUserAndRefreshJwtTokens() throws Exception{
        User user = User.builder()
                .username("user")
                .password("password")
                .build();

        RegisterRequest registerRequest = new RegisterRequest(user.getUsername(), user.getPassword());
        MockHttpServletResponse response = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andReturn()
                .getResponse();

        AuthenticationResponse authResponse = objectMapper.readValue(response.getContentAsString(),
                AuthenticationResponse.class);
        String accessToken = authResponse.accessToken();
        String refreshToken = authResponse.refreshToken();

        RefreshAccessTokenRequest refreshAccessTokenRequest = new RefreshAccessTokenRequest(refreshToken);

        mockMvc.perform(post("/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshAccessTokenRequest))
                        .cookie(new Cookie("Authorization", "Bearer " + accessToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());
    }

}