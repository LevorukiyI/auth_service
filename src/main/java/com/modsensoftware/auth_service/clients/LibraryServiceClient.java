package com.modsensoftware.auth_service.clients;

import com.modsensoftware.auth_service.clients.requests.RegisterLibraryUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class LibraryServiceClient {
    @Value("${library-service.base-url}")
    private String libraryServiceBaseUrl;

    @Value("${library-service.secret-key}")
    private String libraryServiceSecretKey;

    private final RestTemplate restTemplate;

    public ResponseEntity<HttpStatus> registerUser(RegisterLibraryUserRequest registerUserRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", libraryServiceSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterLibraryUserRequest> entity = new HttpEntity<>(registerUserRequest, headers);

        return restTemplate.exchange(
                libraryServiceBaseUrl + "/users/register",
                HttpMethod.POST,
                entity,
                HttpStatus.class
        );
    }
}