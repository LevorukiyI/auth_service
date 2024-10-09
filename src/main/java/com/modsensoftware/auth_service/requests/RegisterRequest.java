package com.modsensoftware.auth_service.requests;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NonNull
    private String username;
    @NonNull
    private String password;
}
