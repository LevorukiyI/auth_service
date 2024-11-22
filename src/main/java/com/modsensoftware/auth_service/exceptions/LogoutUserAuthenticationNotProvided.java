package com.modsensoftware.auth_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LogoutUserAuthenticationNotProvided extends RuntimeException {

    public LogoutUserAuthenticationNotProvided() {
        super("can't logout user which not provided authentication");
    }
}