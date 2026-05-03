package com.bancofortaleza.users.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class BCryptPasswordHashServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void hashShouldDelegateToPasswordEncoder() {
        BCryptPasswordHashServiceImpl service = new BCryptPasswordHashServiceImpl(passwordEncoder);
        when(passwordEncoder.encode("plain-secret")).thenReturn("hashed-secret");

        String result = service.hash("plain-secret");

        assertThat(result).isEqualTo("hashed-secret");
        verify(passwordEncoder).encode("plain-secret");
    }

    @Test
    void hashShouldRejectNullPasswords() {
        BCryptPasswordHashServiceImpl service = new BCryptPasswordHashServiceImpl(passwordEncoder);

        assertThatNullPointerException()
                .isThrownBy(() -> service.hash(null))
                .withMessage("rawPassword must not be null");
    }
}
