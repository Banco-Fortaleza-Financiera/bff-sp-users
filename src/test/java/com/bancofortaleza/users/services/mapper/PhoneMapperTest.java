package com.bancofortaleza.users.services.mapper;

import static com.bancofortaleza.users.TestFixtures.phoneCreateRequest;
import static com.bancofortaleza.users.TestFixtures.phoneEntity;
import static com.bancofortaleza.users.TestFixtures.userEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.bancofortaleza.users.repository.users.entity.PhoneEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bff.services.server.models.PhoneResponse;
import com.bff.services.server.models.Status;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PhoneMapperTest {

    private final PhoneMapper phoneMapper = Mappers.getMapper(PhoneMapper.class);

    @Test
    void toPhoneResponseShouldMapUserIdAndFields() {
        PhoneResponse result = phoneMapper.toPhoneResponse(phoneEntity(1, userEntity(10)));

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getIdUser()).isEqualTo(10);
        assertThat(result.getTypePhone()).isEqualTo("MOBILE");
        assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void toPhoneEntityShouldIgnoreGeneratedFields() {
        PhoneEntity result = phoneMapper.toPhoneEntity(phoneCreateRequest());

        assertThat(result.getId()).isNull();
        assertThat(result.getUser()).isNull();
        assertThat(result.getPhone()).isEqualTo("+502 5555 1234");
        assertThat(result.getStatus()).isEqualTo(UserEntity.Status.ACTIVE);
    }

    @Test
    void listAndUtilityMappingsShouldMapValuesAndNulls() {
        PhoneEntity phoneWithoutUser = phoneEntity(2, null);
        phoneWithoutUser.setStatus(null);

        assertThat(phoneMapper.toPhoneResponse(List.of(phoneEntity(1, userEntity(10))))).hasSize(1);
        assertThat(phoneMapper.toPhoneResponse(phoneWithoutUser).getIdUser()).isNull();
        assertThat(phoneMapper.toPhoneResponse(phoneWithoutUser).getStatus()).isNull();
        assertThat(phoneMapper.toPhoneResponse((PhoneEntity) null)).isNull();
        assertThat(phoneMapper.toPhoneEntity(null)).isNull();
        assertThat(phoneMapper.toPhoneResponse((List<PhoneEntity>) null)).isNull();
        assertThat(phoneMapper.toEntityStatus(null)).isNull();
        assertThat(phoneMapper.toOffsetDateTime(null)).isNull();
        assertThat(phoneMapper.toOffsetDateTime(LocalDateTime.of(2026, 5, 1, 10, 0)))
                .isEqualTo(LocalDateTime.of(2026, 5, 1, 10, 0).atOffset(ZoneOffset.UTC));
    }
}
