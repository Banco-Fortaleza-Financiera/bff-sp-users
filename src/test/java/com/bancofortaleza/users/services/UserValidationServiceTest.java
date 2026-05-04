package com.bancofortaleza.users.services;

import static com.bancofortaleza.users.TestFixtures.userEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.bancofortaleza.users.domain.exceptions.ApiException;
import com.bancofortaleza.users.repository.users.UserRepository;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UserValidationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidationService userValidationService;

    @Test
    void getUserOrThrowShouldReturnUserWhenRepositoryFindsIt() {
        UserEntity user = userEntity(1);
        when(userRepository.getUserById(1)).thenReturn(Optional.of(user));

        UserEntity result = userValidationService.getUserOrThrow(1);

        assertThat(result).isSameAs(user);
    }

    @Test
    void getUserOrThrowShouldThrowNotFoundWhenRepositoryIsEmpty() {
        when(userRepository.getUserById(9)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userValidationService.getUserOrThrow(9))
                .isInstanceOf(ApiException.class)
                .hasMessage("User not found with id: 9")
                .extracting("status", "code")
                .containsExactly(HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }

    @Test
    void getActiveUserOrThrowShouldThrowWhenUserIsInactive() {
        UserEntity user = userEntity(2);
        user.setStatus(UserEntity.Status.INACTIVE);
        when(userRepository.getUserById(2)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userValidationService.getActiveUserOrThrow(2, "Inactive user"))
                .isInstanceOf(ApiException.class)
                .hasMessage("Inactive user")
                .extracting("status", "code")
                .containsExactly(HttpStatus.BAD_REQUEST, "USER_INACTIVE");
    }

    @Test
    void getActiveUserOrThrowShouldReturnActiveUser() {
        UserEntity user = userEntity(2);
        when(userRepository.getUserById(2)).thenReturn(Optional.of(user));

        UserEntity result = userValidationService.getActiveUserOrThrow(2, "Inactive user");

        assertThat(result).isSameAs(user);
    }

    @Test
    void validateAdminUserShouldAllowAdminUsers() {
        UserEntity user = userEntity(3);
        user.setUserType(UserEntity.UserType.ADMIN);
        when(userRepository.getUserById(3)).thenReturn(Optional.of(user));

        userValidationService.validateAdminUser(3);
    }

    @Test
    void validateAdminUserShouldThrowForbiddenForNormalUsers() {
        UserEntity user = userEntity(4);
        user.setUserType(UserEntity.UserType.NORMAL);
        when(userRepository.getUserById(4)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userValidationService.validateAdminUser(4))
                .isInstanceOf(ApiException.class)
                .hasMessage("User with id: 4 is not allowed to execute this operation")
                .extracting("status", "code")
                .containsExactly(HttpStatus.FORBIDDEN, "FORBIDDEN");
    }
}
