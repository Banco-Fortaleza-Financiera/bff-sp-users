package com.bancofortaleza.users.services.mapper;

import static com.bancofortaleza.users.TestFixtures.addressCreateRequest;
import static com.bancofortaleza.users.TestFixtures.addressEntity;
import static com.bancofortaleza.users.TestFixtures.userEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.bancofortaleza.users.repository.users.entity.AddressEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bff.services.server.models.AddressResponse;
import com.bff.services.server.models.Status;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AddressMapperTest {

    private final AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);

    @Test
    void toAddressResponseShouldMapUserIdAndFields() {
        AddressResponse result = addressMapper.toAddressResponse(addressEntity(1, userEntity(10)));

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getIdUser()).isEqualTo(10);
        assertThat(result.getTypeAddress()).isEqualTo("HOME");
        assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void toAddressEntityShouldIgnoreGeneratedFields() {
        AddressEntity result = addressMapper.toAddressEntity(addressCreateRequest());

        assertThat(result.getId()).isNull();
        assertThat(result.getUser()).isNull();
        assertThat(result.getAddress()).isEqualTo("10 Calle 1-23 Zona 10");
        assertThat(result.getStatus()).isEqualTo(UserEntity.Status.ACTIVE);
    }

    @Test
    void listAndUtilityMappingsShouldMapValuesAndNulls() {
        AddressEntity addressWithoutUser = addressEntity(2, null);
        addressWithoutUser.setStatus(null);

        assertThat(addressMapper.toAddressResponse(List.of(addressEntity(1, userEntity(10))))).hasSize(1);
        assertThat(addressMapper.toAddressResponse(addressWithoutUser).getIdUser()).isNull();
        assertThat(addressMapper.toAddressResponse(addressWithoutUser).getStatus()).isNull();
        assertThat(addressMapper.toAddressResponse((AddressEntity) null)).isNull();
        assertThat(addressMapper.toAddressEntity(null)).isNull();
        assertThat(addressMapper.toAddressResponse((List<AddressEntity>) null)).isNull();
        assertThat(addressMapper.toEntityStatus(null)).isNull();
        assertThat(addressMapper.toOffsetDateTime(null)).isNull();
        assertThat(addressMapper.toOffsetDateTime(LocalDateTime.of(2026, 5, 1, 10, 0)))
                .isEqualTo(LocalDateTime.of(2026, 5, 1, 10, 0).atOffset(ZoneOffset.UTC));
    }
}
