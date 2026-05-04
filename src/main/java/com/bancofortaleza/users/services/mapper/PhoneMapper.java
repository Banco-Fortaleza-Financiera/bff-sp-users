package com.bancofortaleza.users.services.mapper;

import com.bancofortaleza.users.repository.users.entity.PhoneEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bff.services.server.models.PhoneCreateRequest;
import com.bff.services.server.models.PhoneResponse;
import com.bff.services.server.models.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PhoneMapper {

    @Mapping(target = "idUser", source = "user.id")
    PhoneResponse toPhoneResponse(PhoneEntity phone);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PhoneEntity toPhoneEntity(PhoneCreateRequest phoneCreateRequest);

    List<PhoneResponse> toPhoneResponse(List<PhoneEntity> phones);

    UserEntity.Status toEntityStatus(Status status);

    default OffsetDateTime toOffsetDateTime(LocalDateTime value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}
