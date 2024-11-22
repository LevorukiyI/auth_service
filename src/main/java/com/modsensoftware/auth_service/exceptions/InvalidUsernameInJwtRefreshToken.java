package com.modsensoftware.auth_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUsernameInJwtRefreshToken extends IllegalArgumentException{
        public InvalidUsernameInJwtRefreshToken() {
            super("Invalid username in refreshToken, username can't be null");
        }
}
