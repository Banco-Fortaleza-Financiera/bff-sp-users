package com.bancofortaleza.users.services.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bancofortaleza.users.services.BCryptPasswordHashService;

import java.util.Objects;

@Service
public class BCryptPasswordHashServiceImpl implements BCryptPasswordHashService {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordHashServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(Objects.requireNonNull(rawPassword, "rawPassword must not be null"));
    }
}
