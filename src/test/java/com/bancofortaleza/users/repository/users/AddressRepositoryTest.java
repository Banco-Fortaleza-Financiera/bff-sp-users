package com.bancofortaleza.users.repository.users;

import static com.bancofortaleza.users.TestFixtures.addressEntity;
import static com.bancofortaleza.users.TestFixtures.userEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bancofortaleza.users.repository.users.entity.AddressEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bancofortaleza.users.repository.users.jpa.AddressJpaRepository;
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
class AddressRepositoryTest {

    @Mock
    private AddressJpaRepository addressJpaRepository;

    @InjectMocks
    private AddressRepository addressRepository;

    @Test
    void createUserAddressShouldSaveAddress() {
        AddressEntity address = addressEntity(null, userEntity(1));
        when(addressJpaRepository.save(address)).thenReturn(address);

        AddressEntity result = addressRepository.createUserAddress(address);

        assertThat(result).isSameAs(address);
    }

    @Test
    @SuppressWarnings("unchecked")
    void listUserAddressesShouldBuildPageable() {
        AddressEntity address = addressEntity(1, userEntity(1));
        when(addressJpaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(address)));

        addressRepository.listUserAddresses(1, null, 150, "home", UserEntity.Status.ACTIVE);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(addressJpaRepository).findAll(any(Specification.class), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(100);
    }

    @Test
    void getUserAddressByIdShouldReturnAddressOrNull() {
        AddressEntity address = addressEntity(2, userEntity(1));
        when(addressJpaRepository.findByIdAndUserId(2, 1)).thenReturn(Optional.of(address));
        when(addressJpaRepository.findByIdAndUserId(3, 1)).thenReturn(Optional.empty());

        assertThat(addressRepository.getUserAddressById(1, 2)).isSameAs(address);
        assertThat(addressRepository.getUserAddressById(1, 3)).isNull();
    }

    @Test
    void updateAddressStatusShouldSaveWhenAddressExists() {
        AddressEntity address = addressEntity(2, userEntity(1));
        when(addressJpaRepository.findByIdAndUserId(2, 1)).thenReturn(Optional.of(address));
        when(addressJpaRepository.save(address)).thenReturn(address);

        AddressEntity result = addressRepository.updateAddressStatus(1, 2, UserEntity.Status.INACTIVE);

        assertThat(result).isSameAs(address);
        assertThat(address.getStatus()).isEqualTo(UserEntity.Status.INACTIVE);
    }

    @Test
    void updateAddressStatusShouldReturnNullWhenAddressDoesNotExist() {
        when(addressJpaRepository.findByIdAndUserId(99, 1)).thenReturn(Optional.empty());

        assertThat(addressRepository.updateAddressStatus(1, 99, UserEntity.Status.INACTIVE)).isNull();
    }
}
