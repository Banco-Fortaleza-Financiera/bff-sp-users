package com.bancofortaleza.users.services;

public interface BCryptPasswordHashService {

    String hash(String rawPassword);
}
