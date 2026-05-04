package com.bancofortaleza.users.services.mapper;

import static com.bancofortaleza.users.TestFixtures.addressCreateRequest;
import static com.bancofortaleza.users.TestFixtures.addressEntity;
import static com.bancofortaleza.users.TestFixtures.phoneCreateRequest;
import static com.bancofortaleza.users.TestFixtures.phoneEntity;
import static com.bancofortaleza.users.TestFixtures.userCreateRequest;
import static com.bancofortaleza.users.TestFixtures.userEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.bancofortaleza.users.repository.users.entity.AddressEntity;
import com.bancofortaleza.users.repository.users.entity.PhoneEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bff.services.server.models.AddressResponse;
import com.bff.services.server.models.PhoneResponse;
import com.bff.services.server.models.Status;
import com.bff.services.server.models.UserResponse;
import com.bff.services.server.models.UserType;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toUserResponseShouldMapUserAndChildren() {
        UserEntity user = userEntity(1);
        AddressEntity address = addressEntity(10, user);
        PhoneEntity phone = phoneEntity(20, user);
        user.setAddresses(List.of(address));
        user.setPhones(List.of(phone));

        UserResponse result = userMapper.toUserResponse(user);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(result.getUserType()).isEqualTo(UserType.ADMIN);
        assertThat(result.getCreatedAt()).isEqualTo(user.getCreatedAt().atOffset(ZoneOffset.UTC));
        assertThat(result.getAddresses()).hasSize(1);
        assertThat(result.getPhones()).hasSize(1);
    }

    @Test
    void toUserResponseShouldMapAlternateEnumsAndNullChildren() {
        UserEntity user = userEntity(2);
        user.setGender(UserEntity.Gender.FEMALE);
        user.setStatus(UserEntity.Status.INACTIVE);
        user.setUserType(UserEntity.UserType.NORMAL);
        user.setAddresses(null);
        user.setPhones(null);

        UserResponse result = userMapper.toUserResponse(user);

        assertThat(result.getGender()).isEqualTo(com.bff.services.server.models.Gender.FEMALE);
        assertThat(result.getStatus()).isEqualTo(Status.INACTIVE);
        assertThat(result.getUserType()).isEqualTo(UserType.NORMAL);
        assertThat(result.getAddresses()).isNull();
        assertThat(result.getPhones()).isNull();
    }

    @Test
    void toUserCreateRequestShouldMapRequestAndAttachChildren() {
        UserEntity result = userMapper.toUserCreateRequest(userCreateRequest());

        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("Gerson");
        assertThat(result.getGender()).isEqualTo(UserEntity.Gender.MALE);
        assertThat(result.getStatus()).isEqualTo(UserEntity.Status.ACTIVE);
        assertThat(result.getUserType()).isEqualTo(UserEntity.UserType.ADMIN);
        assertThat(result.getAddresses()).hasSize(1)
                .allSatisfy(address -> assertThat(address.getUser()).isSameAs(result));
        assertThat(result.getPhones()).hasSize(1)
                .allSatisfy(phone -> assertThat(phone.getUser()).isSameAs(result));
    }

    @Test
    void childMappingMethodsShouldMapUserIdAndFields() {
        UserEntity user = userEntity(1);
        AddressResponse address = userMapper.toAddressResponse(addressEntity(10, user));
        PhoneResponse phone = userMapper.toPhoneResponse(phoneEntity(20, user));

        assertThat(address.getIdUser()).isEqualTo(1);
        assertThat(address.getAddress()).isEqualTo("10 Calle 1-23 Zona 10");
        assertThat(phone.getIdUser()).isEqualTo(1);
        assertThat(phone.getPhone()).isEqualTo("+502 5555 1234");
    }

    @Test
    void requestChildMappingMethodsShouldIgnoreDatabaseFields() {
        AddressEntity address = userMapper.toAddressEntity(addressCreateRequest());
        PhoneEntity phone = userMapper.toPhoneEntity(phoneCreateRequest());

        assertThat(address.getId()).isNull();
        assertThat(address.getUser()).isNull();
        assertThat(address.getAddress()).isEqualTo("10 Calle 1-23 Zona 10");
        assertThat(phone.getId()).isNull();
        assertThat(phone.getUser()).isNull();
        assertThat(phone.getTypePhone()).isEqualTo("MOBILE");
    }

    @Test
    void enumAndDateMappingsShouldHandleNulls() {
        assertThat(userMapper.toUserResponse(null)).isNull();
        assertThat(userMapper.toUserCreateRequest(null)).isNull();
        assertThat(userMapper.toUserResponses(null)).isNull();
        assertThat(userMapper.toAddressResponse(null)).isNull();
        assertThat(userMapper.toPhoneResponse(null)).isNull();
        assertThat(userMapper.toAddressEntity(null)).isNull();
        assertThat(userMapper.toPhoneEntity(null)).isNull();
        assertThat(userMapper.toEntityStatus(null)).isNull();
        assertThat(userMapper.toEntityUserType(null)).isNull();
        assertThat(userMapper.toOffsetDateTime(null)).isNull();
        assertThat(userMapper.toOffsetDateTime(LocalDateTime.of(2026, 5, 1, 10, 0)))
                .isEqualTo(LocalDateTime.of(2026, 5, 1, 10, 0).atOffset(ZoneOffset.UTC));
    }

    @Test
    void childResponseMappingShouldHandleMissingUserAndNullEnums() {
        AddressEntity address = addressEntity(10, null);
        PhoneEntity phone = phoneEntity(20, null);
        address.setStatus(null);
        phone.setStatus(null);

        AddressResponse addressResponse = userMapper.toAddressResponse(address);
        PhoneResponse phoneResponse = userMapper.toPhoneResponse(phone);

        assertThat(addressResponse.getIdUser()).isNull();
        assertThat(addressResponse.getStatus()).isNull();
        assertThat(phoneResponse.getIdUser()).isNull();
        assertThat(phoneResponse.getStatus()).isNull();
    }
}
