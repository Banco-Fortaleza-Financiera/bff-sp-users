package com.bancofortaleza.users.repository.users.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserEntityTest {

    @Test
    void addAddressAndAddPhoneShouldMaintainBidirectionalRelation() {
        UserEntity user = new UserEntity();
        AddressEntity address = new AddressEntity();
        PhoneEntity phone = new PhoneEntity();

        user.addAddress(address);
        user.addPhone(phone);

        assertThat(user.getAddresses()).containsExactly(address);
        assertThat(user.getPhones()).containsExactly(phone);
        assertThat(address.getUser()).isSameAs(user);
        assertThat(phone.getUser()).isSameAs(user);
    }

    @Test
    void prePersistShouldSetDefaultsWhenFieldsAreNull() {
        UserEntity user = new UserEntity();
        user.setStatus(null);
        user.setUserType(null);

        user.prePersist();

        assertThat(user.getStatus()).isEqualTo(UserEntity.Status.ACTIVE);
        assertThat(user.getUserType()).isEqualTo(UserEntity.UserType.NORMAL);
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void prePersistShouldKeepExistingDefaultsWhenFieldsArePresent() {
        UserEntity user = new UserEntity();
        user.setStatus(UserEntity.Status.INACTIVE);
        user.setUserType(UserEntity.UserType.ADMIN);
        java.time.LocalDateTime createdAt = java.time.LocalDateTime.of(2026, 5, 1, 10, 0);
        user.setCreatedAt(createdAt);

        user.prePersist();

        assertThat(user.getStatus()).isEqualTo(UserEntity.Status.INACTIVE);
        assertThat(user.getUserType()).isEqualTo(UserEntity.UserType.ADMIN);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void preUpdateShouldSetUpdatedAt() {
        UserEntity user = new UserEntity();

        user.preUpdate();

        assertThat(user.getUpdatedAt()).isNotNull();
    }
}
