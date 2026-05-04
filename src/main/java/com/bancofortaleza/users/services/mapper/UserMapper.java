package com.bancofortaleza.users.services.mapper;

import com.bancofortaleza.users.repository.users.entity.AddressEntity;
import com.bancofortaleza.users.repository.users.entity.PhoneEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bff.services.server.models.AddressCreateRequest;
import com.bff.services.server.models.AddressResponse;
import com.bff.services.server.models.PhoneCreateRequest;
import com.bff.services.server.models.PhoneResponse;
import com.bff.services.server.models.Status;
import com.bff.services.server.models.UserCreateRequest;
import com.bff.services.server.models.UserResponse;
import com.bff.services.server.models.UserType;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(UserEntity user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toUserCreateRequest(UserCreateRequest userCreateRequest);

    List<UserResponse> toUserResponses(List<UserEntity> users);

    @Mapping(target = "idUser", source = "user.id")
    AddressResponse toAddressResponse(AddressEntity address);

    @Mapping(target = "idUser", source = "user.id")
    PhoneResponse toPhoneResponse(PhoneEntity phone);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AddressEntity toAddressEntity(AddressCreateRequest addressCreateRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PhoneEntity toPhoneEntity(PhoneCreateRequest phoneCreateRequest);

    UserEntity.Status toEntityStatus(Status status);

    UserEntity.UserType toEntityUserType(UserType userType);

    @AfterMapping
    default void setUserOnChildren(@MappingTarget UserEntity user) {
        if (user.getAddresses() != null) {
            user.getAddresses().forEach(address -> address.setUser(user));
        }
        if (user.getPhones() != null) {
            user.getPhones().forEach(phone -> phone.setUser(user));
        }
    }

    default OffsetDateTime toOffsetDateTime(LocalDateTime value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}
