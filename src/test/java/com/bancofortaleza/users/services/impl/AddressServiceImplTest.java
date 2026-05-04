package com.bancofortaleza.users.services.impl;

import static com.bancofortaleza.users.TestFixtures.addressCreateRequest;
import static com.bancofortaleza.users.TestFixtures.addressEntity;
import static com.bancofortaleza.users.TestFixtures.addressResponse;
import static com.bancofortaleza.users.TestFixtures.userEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bancofortaleza.users.domain.exceptions.ApiException;
import com.bancofortaleza.users.repository.users.AddressRepository;
import com.bancofortaleza.users.repository.users.entity.AddressEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bancofortaleza.users.services.UserValidationService;
import com.bancofortaleza.users.services.mapper.AddressMapper;
import com.bff.services.server.models.AddressResponse;
import com.bff.services.server.models.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void listUserAddressesShouldValidateActiveUserAndMapPage() {
        UserEntity user = userEntity(1);
        AddressEntity address = addressEntity(2, user);
        AddressResponse response = addressResponse(2, 1);
        when(userValidationService.getActiveUserOrThrow(1, "Cannot list addresses for an inactive user with id: 1"))
                .thenReturn(user);
        when(addressMapper.toEntityStatus(Status.ACTIVE)).thenReturn(UserEntity.Status.ACTIVE);
        when(addressRepository.listUserAddresses(1, 1, 10, "home", UserEntity.Status.ACTIVE))
                .thenReturn(new PageImpl<>(java.util.List.of(address)));
        when(addressMapper.toAddressResponse(address)).thenReturn(response);

        Page<AddressResponse> result = addressService.listUserAddresses(1, 1, 10, "home", Status.ACTIVE);

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    void createUserAddressShouldAttachActiveUserBeforeSaving() {
        UserEntity user = userEntity(1);
        AddressEntity mappedAddress = addressEntity(null, null);
        AddressEntity savedAddress = addressEntity(3, user);
        AddressResponse response = addressResponse(3, 1);
        when(userValidationService.getActiveUserOrThrow(1, "Cannot add address to an inactive user with id: 1"))
                .thenReturn(user);
        when(addressMapper.toAddressEntity(addressCreateRequest())).thenReturn(mappedAddress);
        when(addressRepository.createUserAddress(mappedAddress)).thenReturn(savedAddress);
        when(addressMapper.toAddressResponse(savedAddress)).thenReturn(response);

        AddressResponse result = addressService.createUserAddress(1, addressCreateRequest());

        ArgumentCaptor<AddressEntity> addressCaptor = ArgumentCaptor.forClass(AddressEntity.class);
        verify(addressRepository).createUserAddress(addressCaptor.capture());
        assertThat(addressCaptor.getValue().getUser()).isSameAs(user);
        assertThat(result).isSameAs(response);
    }

    @Test
    void getUserAddressByIdShouldThrowWhenAddressDoesNotExistForUser() {
        when(userValidationService.getActiveUserOrThrow(1, "Cannot get address for an inactive user with id: 1"))
                .thenReturn(userEntity(1));
        when(addressRepository.getUserAddressById(1, 99)).thenReturn(null);

        assertThatThrownBy(() -> addressService.getUserAddressById(1, 99))
                .isInstanceOf(ApiException.class)
                .hasMessage("Address not found with id: 99 for user id: 1")
                .extracting("status", "code")
                .containsExactly(HttpStatus.NOT_FOUND, "ADDRESS_NOT_FOUND");
    }

    @Test
    void getUserAddressByIdShouldReturnMappedAddressWhenItExists() {
        UserEntity user = userEntity(1);
        AddressEntity address = addressEntity(2, user);
        AddressResponse response = addressResponse(2, 1);
        when(userValidationService.getActiveUserOrThrow(1, "Cannot get address for an inactive user with id: 1"))
                .thenReturn(user);
        when(addressRepository.getUserAddressById(1, 2)).thenReturn(address);
        when(addressMapper.toAddressResponse(address)).thenReturn(response);

        AddressResponse result = addressService.getUserAddressById(1, 2);

        assertThat(result).isSameAs(response);
    }

    @Test
    void updateUserAddressStatusShouldPersistNewStatus() {
        UserEntity user = userEntity(1);
        AddressEntity address = addressEntity(4, user);
        AddressEntity savedAddress = addressEntity(4, user);
        savedAddress.setStatus(UserEntity.Status.INACTIVE);
        AddressResponse response = addressResponse(4, 1).status(Status.INACTIVE);
        when(userValidationService.getActiveUserOrThrow(1, "Cannot update address for an inactive user with id: 1"))
                .thenReturn(user);
        when(addressRepository.getUserAddressById(1, 4)).thenReturn(address);
        when(addressMapper.toEntityStatus(Status.INACTIVE)).thenReturn(UserEntity.Status.INACTIVE);
        when(addressRepository.updateAddressStatus(1, 4, UserEntity.Status.INACTIVE)).thenReturn(savedAddress);
        when(addressMapper.toAddressResponse(savedAddress)).thenReturn(response);

        AddressResponse result = addressService.updateUserAddressStatus(1, 4, Status.INACTIVE);

        assertThat(address.getStatus()).isEqualTo(UserEntity.Status.INACTIVE);
        assertThat(result).isSameAs(response);
    }
}
