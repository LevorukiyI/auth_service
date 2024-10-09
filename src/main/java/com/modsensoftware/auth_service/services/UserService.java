package com.modsensoftware.auth_service.services;

import com.modsensoftware.auth_service.models.User;
import com.modsensoftware.auth_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("Invalid Username"));
    }
}
