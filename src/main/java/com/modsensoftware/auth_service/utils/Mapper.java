package com.modsensoftware.auth_service.utils;

import com.modsensoftware.auth_service.clients.requests.RegisterLibraryUserRequest;
import com.modsensoftware.auth_service.models.User;

public class Mapper {
    public static RegisterLibraryUserRequest from(User user){
        return RegisterLibraryUserRequest.builder()
                .subject(user.getUsername())
                .build();
    }
}
