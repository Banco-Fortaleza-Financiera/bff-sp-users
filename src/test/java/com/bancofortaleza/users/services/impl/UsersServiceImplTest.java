package com.bancofortaleza.users.services.impl;

import static com.bancofortaleza.users.TestFixtures.userCreateRequest;
import static com.bancofortaleza.users.TestFixtures.userEntity;
import static com.bancofortaleza.users.TestFixtures.userResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bancofortaleza.users.domain.exceptions.ApiException;
import com.bancofortaleza.users.repository.users.UserRepository;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bancofortaleza.users.services.BCryptPasswordHashService;
import com.bancofortaleza.users.services.UserValidationService;
import com.bancofortaleza.users.services.mapper.UserMapper;
import com.bff.services.server.models.Status;
import com.bff.services.server.models.UserCreateRequest;
import com.bff.services.server.models.UserResponse;
import com.bff.services.server.models.UserType;
import java.util.Optional;
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
class UsersServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordHashService passwordHashService;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private UsersServiceImpl usersService;

    @Test
    void listUsersShouldMapFiltersAndResponses() {
        UserEntity user = userEntity(1);
        UserResponse response = userResponse(1);
        when(userMapper.toEntityStatus(Status.ACTIVE)).thenReturn(UserEntity.Status.ACTIVE);
        when(userMapper.toEntityUserType(UserType.ADMIN)).thenReturn(UserEntity.UserType.ADMIN);
        when(userRepository.listUsers(1, 10, "ger", UserEntity.Status.ACTIVE, UserEntity.UserType.ADMIN))
                .thenReturn(new PageImpl<>(java.util.List.of(user)));
        when(userMapper.toUserResponse(user)).thenReturn(response);

        Page<UserResponse> result = usersService.listUsers(1, 10, "ger", Status.ACTIVE, UserType.ADMIN);

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    void createUserShouldHashPasswordBeforeSaving() {
        UserCreateRequest request = userCreateRequest();
        UserEntity mappedUser = userEntity(null);
        UserEntity savedUser = userEntity(1);
        UserResponse expectedResponse = userResponse(1);
        when(userMapper.toUserCreateRequest(request)).thenReturn(mappedUser);
        when(passwordHashService.hash("raw-password")).thenReturn("hashed-password");
        when(userRepository.createUser(any(UserEntity.class))).thenReturn(savedUser);
        when(userMapper.toUserResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse result = usersService.createUser(request);

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).createUser(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("hashed-password");
        assertThat(result).isSameAs(expectedResponse);
    }

    @Test
    void getUserByIdShouldReturnMappedUserWhenExists() {
        UserEntity user = userEntity(5);
        UserResponse response = userResponse(5);
        when(userValidationService.getUserOrThrow(5)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(response);

        UserResponse result = usersService.getUserById(5);

        assertThat(result).isSameAs(response);
    }

    @Test
    void updateUserStatusShouldReturnMappedUserWhenRepositoryUpdates() {
        UserEntity user = userEntity(7);
        UserResponse response = userResponse(7);
        when(userMapper.toEntityStatus(Status.INACTIVE)).thenReturn(UserEntity.Status.INACTIVE);
        when(userRepository.updateUserStatus(7, UserEntity.Status.INACTIVE)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(response);

        UserResponse result = usersService.updateUserStatus(7, Status.INACTIVE);

        assertThat(result).isSameAs(response);
    }

    @Test
    void updateUserStatusShouldThrowWhenUserDoesNotExist() {
        ApiException exception = new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found");
        when(userMapper.toEntityStatus(Status.INACTIVE)).thenReturn(UserEntity.Status.INACTIVE);
        when(userRepository.updateUserStatus(9, UserEntity.Status.INACTIVE)).thenReturn(Optional.empty());
        when(userValidationService.userNotFoundException(9)).thenReturn(exception);

        assertThatThrownBy(() -> usersService.updateUserStatus(9, Status.INACTIVE))
                .isSameAs(exception);
    }
}
