package com.bancofortaleza.users.repository.users.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PhoneEntityTest {

    @Test
    void prePersistShouldSetDefaultStatusAndCreatedAtWhenMissing() {
        PhoneEntity phone = new PhoneEntity();
        phone.setStatus(null);

        phone.prePersist();

        assertThat(phone.getStatus()).isEqualTo(UserEntity.Status.ACTIVE);
        assertThat(phone.getCreatedAt()).isNotNull();
    }

    @Test
    void prePersistShouldKeepExistingValues() {
        PhoneEntity phone = new PhoneEntity();
        java.time.LocalDateTime createdAt = java.time.LocalDateTime.of(2026, 5, 1, 10, 0);
        phone.setStatus(UserEntity.Status.INACTIVE);
        phone.setCreatedAt(createdAt);

        phone.prePersist();

        assertThat(phone.getStatus()).isEqualTo(UserEntity.Status.INACTIVE);
        assertThat(phone.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void preUpdateShouldSetUpdatedAt() {
        PhoneEntity phone = new PhoneEntity();

        phone.preUpdate();

        assertThat(phone.getUpdatedAt()).isNotNull();
    }
}
