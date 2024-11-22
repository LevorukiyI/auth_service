package com.modsensoftware.auth_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JwtRefreshTokenNotProvidedException extends IllegalArgumentException{
    public JwtRefreshTokenNotProvidedException() {
        super("jwt refresh token no provided in request");
    }
}
