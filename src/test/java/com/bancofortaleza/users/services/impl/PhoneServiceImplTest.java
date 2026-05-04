package com.bancofortaleza.users.services.impl;

import static com.bancofortaleza.users.TestFixtures.phoneCreateRequest;
import static com.bancofortaleza.users.TestFixtures.phoneEntity;
import static com.bancofortaleza.users.TestFixtures.phoneResponse;
import static com.bancofortaleza.users.TestFixtures.userEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bancofortaleza.users.domain.exceptions.ApiException;
import com.bancofortaleza.users.repository.users.PhoneRepository;
import com.bancofortaleza.users.repository.users.entity.PhoneEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bancofortaleza.users.services.UserValidationService;
import com.bancofortaleza.users.services.mapper.PhoneMapper;
import com.bff.services.server.models.PhoneResponse;
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
class PhoneServiceImplTest {

    @Mock
    private PhoneRepository phoneRepository;

    @Mock
    private PhoneMapper phoneMapper;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private PhoneServiceImpl phoneService;

    @Test
    void listUserPhonesShouldValidateActiveUserAndMapPage() {
        UserEntity user = userEntity(1);
        PhoneEntity phone = phoneEntity(2, user);
        PhoneResponse response = phoneResponse(2, 1);
        when(userValidationService.getActiveUserOrThrow(1, "Cannot list phones for an inactive user with id: 1"))
                .thenReturn(user);
        when(phoneMapper.toEntityStatus(Status.ACTIVE)).thenReturn(UserEntity.Status.ACTIVE);
        when(phoneRepository.listUserPhones(1, 1, 10, "5555", UserEntity.Status.ACTIVE))
                .thenReturn(new PageImpl<>(java.util.List.of(phone)));
        when(phoneMapper.toPhoneResponse(phone)).thenReturn(response);

        Page<PhoneResponse> result = phoneService.listUserPhones(1, 1, 10, "5555", Status.ACTIVE);

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    void createUserPhoneShouldAttachActiveUserBeforeSaving() {
        UserEntity user = userEntity(1);
        PhoneEntity mappedPhone = phoneEntity(null, null);
        PhoneEntity savedPhone = phoneEntity(3, user);
        PhoneResponse response = phoneResponse(3, 1);
        when(userValidationService.getActiveUserOrThrow(1, "Cannot add phone to an inactive user with id: 1"))
                .thenReturn(user);
        when(phoneMapper.toPhoneEntity(phoneCreateRequest())).thenReturn(mappedPhone);
        when(phoneRepository.createUserPhone(mappedPhone)).thenReturn(savedPhone);
        when(phoneMapper.toPhoneResponse(savedPhone)).thenReturn(response);

        PhoneResponse result = phoneService.createUserPhone(1, phoneCreateRequest());

        ArgumentCaptor<PhoneEntity> phoneCaptor = ArgumentCaptor.forClass(PhoneEntity.class);
        verify(phoneRepository).createUserPhone(phoneCaptor.capture());
        assertThat(phoneCaptor.getValue().getUser()).isSameAs(user);
        assertThat(result).isSameAs(response);
    }

    @Test
    void getUserPhoneByIdShouldThrowWhenPhoneDoesNotExistForUser() {
        when(userValidationService.getActiveUserOrThrow(1, "Cannot get phone for an inactive user with id: 1"))
                .thenReturn(userEntity(1));
        when(phoneRepository.getUserPhoneById(1, 99)).thenReturn(null);

        assertThatThrownBy(() -> phoneService.getUserPhoneById(1, 99))
                .isInstanceOf(ApiException.class)
                .hasMessage("Phone not found with id: 99 for user id: 1")
                .extracting("status", "code")
                .containsExactly(HttpStatus.NOT_FOUND, "PHONE_NOT_FOUND");
    }

    @Test
    void getUserPhoneByIdShouldReturnMappedPhoneWhenItExists() {
        UserEntity user = userEntity(1);
        PhoneEntity phone = phoneEntity(2, user);
        PhoneResponse response = phoneResponse(2, 1);
        when(userValidationService.getActiveUserOrThrow(1, "Cannot get phone for an inactive user with id: 1"))
                .thenReturn(user);
        when(phoneRepository.getUserPhoneById(1, 2)).thenReturn(phone);
        when(phoneMapper.toPhoneResponse(phone)).thenReturn(response);

        PhoneResponse result = phoneService.getUserPhoneById(1, 2);

        assertThat(result).isSameAs(response);
    }

    @Test
    void updateUserPhoneStatusShouldPersistNewStatus() {
        UserEntity user = userEntity(1);
        PhoneEntity phone = phoneEntity(4, user);
        PhoneEntity savedPhone = phoneEntity(4, user);
        savedPhone.setStatus(UserEntity.Status.INACTIVE);
        PhoneResponse response = phoneResponse(4, 1).status(Status.INACTIVE);
        when(userValidationService.getActiveUserOrThrow(1, "Cannot update phone for an inactive user with id: 1"))
                .thenReturn(user);
        when(phoneRepository.getUserPhoneById(1, 4)).thenReturn(phone);
        when(phoneMapper.toEntityStatus(Status.INACTIVE)).thenReturn(UserEntity.Status.INACTIVE);
        when(phoneRepository.updatePhoneStatus(1, 4, UserEntity.Status.INACTIVE)).thenReturn(savedPhone);
        when(phoneMapper.toPhoneResponse(savedPhone)).thenReturn(response);

        PhoneResponse result = phoneService.updateUserPhoneStatus(1, 4, Status.INACTIVE);

        assertThat(phone.getStatus()).isEqualTo(UserEntity.Status.INACTIVE);
        assertThat(result).isSameAs(response);
    }
}
