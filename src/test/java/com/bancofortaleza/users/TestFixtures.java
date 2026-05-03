package com.bancofortaleza.users;

import com.bancofortaleza.users.repository.users.entity.AddressEntity;
import com.bancofortaleza.users.repository.users.entity.PhoneEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bff.services.server.models.AddressCreateRequest;
import com.bff.services.server.models.AddressResponse;
import com.bff.services.server.models.Gender;
import com.bff.services.server.models.PhoneCreateRequest;
import com.bff.services.server.models.PhoneResponse;
import com.bff.services.server.models.Status;
import com.bff.services.server.models.UserCreateRequest;
import com.bff.services.server.models.UserResponse;
import com.bff.services.server.models.UserType;
import java.time.LocalDateTime;
import java.util.List;

public final class TestFixtures {

    private TestFixtures() {
    }

    public static UserEntity userEntity(Integer id) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setName("Gerson");
        user.setLastName("Ramos");
        user.setPassword("raw-password");
        user.setGender(UserEntity.Gender.MALE);
        user.setAge(30);
        user.setIdentification("1234567890101");
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setUserType(UserEntity.UserType.ADMIN);
        user.setCreatedAt(LocalDateTime.of(2026, 5, 1, 10, 0));
        return user;
    }

    public static AddressEntity addressEntity(Integer id, UserEntity user) {
        AddressEntity address = new AddressEntity();
        address.setId(id);
        address.setUser(user);
        address.setAddress("10 Calle 1-23 Zona 10");
        address.setTypeAddress("HOME");
        address.setStatus(UserEntity.Status.ACTIVE);
        address.setCreatedAt(LocalDateTime.of(2026, 5, 1, 11, 0));
        return address;
    }

    public static PhoneEntity phoneEntity(Integer id, UserEntity user) {
        PhoneEntity phone = new PhoneEntity();
        phone.setId(id);
        phone.setUser(user);
        phone.setPhone("+502 5555 1234");
        phone.setTypePhone("MOBILE");
        phone.setStatus(UserEntity.Status.ACTIVE);
        phone.setCreatedAt(LocalDateTime.of(2026, 5, 1, 12, 0));
        return phone;
    }

    public static UserCreateRequest userCreateRequest() {
        return new UserCreateRequest()
                .name("Gerson")
                .lastName("Ramos")
                .password("raw-password")
                .gender(Gender.MALE)
                .age(30)
                .identification("1234567890101")
                .status(Status.ACTIVE)
                .userType(UserType.ADMIN)
                .addresses(List.of(addressCreateRequest()))
                .phones(List.of(phoneCreateRequest()));
    }

    public static AddressCreateRequest addressCreateRequest() {
        return new AddressCreateRequest()
                .address("10 Calle 1-23 Zona 10")
                .typeAddress("HOME")
                .status(Status.ACTIVE);
    }

    public static PhoneCreateRequest phoneCreateRequest() {
        return new PhoneCreateRequest()
                .phone("+502 5555 1234")
                .typePhone("MOBILE")
                .status(Status.ACTIVE);
    }

    public static UserResponse userResponse(Integer id) {
        return new UserResponse()
                .id(id)
                .name("Gerson")
                .lastName("Ramos")
                .gender(Gender.MALE)
                .age(30)
                .identification("1234567890101")
                .status(Status.ACTIVE)
                .userType(UserType.ADMIN);
    }

    public static AddressResponse addressResponse(Integer id, Integer userId) {
        return new AddressResponse()
                .id(id)
                .idUser(userId)
                .address("10 Calle 1-23 Zona 10")
                .typeAddress("HOME")
                .status(Status.ACTIVE);
    }

    public static PhoneResponse phoneResponse(Integer id, Integer userId) {
        return new PhoneResponse()
                .id(id)
                .idUser(userId)
                .phone("+502 5555 1234")
                .typePhone("MOBILE")
                .status(Status.ACTIVE);
    }
}
