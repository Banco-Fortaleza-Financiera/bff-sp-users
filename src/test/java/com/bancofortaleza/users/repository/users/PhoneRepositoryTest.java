package com.bancofortaleza.users.repository.users;

import static com.bancofortaleza.users.TestFixtures.phoneEntity;
import static com.bancofortaleza.users.TestFixtures.userEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bancofortaleza.users.repository.users.entity.PhoneEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bancofortaleza.users.repository.users.jpa.PhoneJpaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class PhoneRepositoryTest {

    @Mock
    private PhoneJpaRepository phoneJpaRepository;

    @InjectMocks
    private PhoneRepository phoneRepository;

    @Test
    void createUserPhoneShouldSavePhone() {
        PhoneEntity phone = phoneEntity(null, userEntity(1));
        when(phoneJpaRepository.save(phone)).thenReturn(phone);

        PhoneEntity result = phoneRepository.createUserPhone(phone);

        assertThat(result).isSameAs(phone);
    }

    @Test
    @SuppressWarnings("unchecked")
    void listUserPhonesShouldBuildPageable() {
        PhoneEntity phone = phoneEntity(1, userEntity(1));
        when(phoneJpaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(phone)));

        phoneRepository.listUserPhones(1, -1, 0, "5555", UserEntity.Status.ACTIVE);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(phoneJpaRepository).findAll(any(Specification.class), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
    }

    @Test
    void getUserPhoneByIdShouldReturnPhoneOrNull() {
        PhoneEntity phone = phoneEntity(2, userEntity(1));
        when(phoneJpaRepository.findByIdAndUserId(2, 1)).thenReturn(Optional.of(phone));
        when(phoneJpaRepository.findByIdAndUserId(3, 1)).thenReturn(Optional.empty());

        assertThat(phoneRepository.getUserPhoneById(1, 2)).isSameAs(phone);
        assertThat(phoneRepository.getUserPhoneById(1, 3)).isNull();
    }

    @Test
    void updatePhoneStatusShouldSaveWhenPhoneExists() {
        PhoneEntity phone = phoneEntity(2, userEntity(1));
        when(phoneJpaRepository.findByIdAndUserId(2, 1)).thenReturn(Optional.of(phone));
        when(phoneJpaRepository.save(phone)).thenReturn(phone);

        PhoneEntity result = phoneRepository.updatePhoneStatus(1, 2, UserEntity.Status.INACTIVE);

        assertThat(result).isSameAs(phone);
        assertThat(phone.getStatus()).isEqualTo(UserEntity.Status.INACTIVE);
    }

    @Test
    void updatePhoneStatusShouldReturnNullWhenPhoneDoesNotExist() {
        when(phoneJpaRepository.findByIdAndUserId(99, 1)).thenReturn(Optional.empty());

        assertThat(phoneRepository.updatePhoneStatus(1, 99, UserEntity.Status.INACTIVE)).isNull();
    }
}
