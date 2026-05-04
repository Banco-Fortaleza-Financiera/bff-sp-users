package com.bancofortaleza.users.services.mapper;

import com.bancofortaleza.users.repository.users.entity.AddressEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bff.services.server.models.AddressCreateRequest;
import com.bff.services.server.models.AddressResponse;
import com.bff.services.server.models.Status;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "idUser", source = "user.id")
    AddressResponse toAddressResponse(AddressEntity address);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AddressEntity toAddressEntity(AddressCreateRequest addressCreateRequest);

    List<AddressResponse> toAddressResponse(List<AddressEntity> addresses);

    UserEntity.Status toEntityStatus(Status status);

    default OffsetDateTime toOffsetDateTime(LocalDateTime value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}
