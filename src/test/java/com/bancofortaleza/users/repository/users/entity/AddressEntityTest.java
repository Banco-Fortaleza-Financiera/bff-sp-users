package com.bancofortaleza.users.repository.users.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AddressEntityTest {

    @Test
    void prePersistShouldSetDefaultStatusAndCreatedAtWhenMissing() {
        AddressEntity address = new AddressEntity();
        address.setStatus(null);

        address.prePersist();

        assertThat(address.getStatus()).isEqualTo(UserEntity.Status.ACTIVE);
        assertThat(address.getCreatedAt()).isNotNull();
    }

    @Test
    void prePersistShouldKeepExistingValues() {
        AddressEntity address = new AddressEntity();
        java.time.LocalDateTime createdAt = java.time.LocalDateTime.of(2026, 5, 1, 10, 0);
        address.setStatus(UserEntity.Status.INACTIVE);
        address.setCreatedAt(createdAt);

        address.prePersist();

        assertThat(address.getStatus()).isEqualTo(UserEntity.Status.INACTIVE);
        assertThat(address.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void preUpdateShouldSetUpdatedAt() {
        AddressEntity address = new AddressEntity();

        address.preUpdate();

        assertThat(address.getUpdatedAt()).isNotNull();
    }
}
