package com.bancofortaleza.users.repository.users;

import static com.bancofortaleza.users.TestFixtures.addressEntity;
import static com.bancofortaleza.users.TestFixtures.phoneEntity;
import static com.bancofortaleza.users.TestFixtures.userEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bancofortaleza.users.repository.users.entity.AddressEntity;
import com.bancofortaleza.users.repository.users.entity.PhoneEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bancofortaleza.users.repository.users.jpa.UserJpaRepository;
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
class UserRepositoryTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private UserRepository userRepository;

    @Test
    void createUserShouldAttachChildrenBeforeSaving() {
        UserEntity user = userEntity(null);
        AddressEntity address = addressEntity(null, null);
        PhoneEntity phone = phoneEntity(null, null);
        user.setAddresses(List.of(address));
        user.setPhones(List.of(phone));
        when(userJpaRepository.save(user)).thenReturn(user);

        UserEntity result = userRepository.createUser(user);

        assertThat(result).isSameAs(user);
        assertThat(address.getUser()).isSameAs(user);
        assertThat(phone.getUser()).isSameAs(user);
    }

    @Test
    @SuppressWarnings("unchecked")
    void listUsersShouldUseOneBasedHeadersAsPageable() {
        UserEntity user = userEntity(1);
        when(userJpaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));

        userRepository.listUsers(2, 50, "ger", UserEntity.Status.ACTIVE, UserEntity.UserType.ADMIN);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userJpaRepository).findAll(any(Specification.class), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(1);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(50);
        assertThat(pageableCaptor.getValue().getSort().getOrderFor("id")).isNotNull();
    }

    @Test
    void getUserByIdShouldDelegateToJpaRepository() {
        UserEntity user = userEntity(3);
        when(userJpaRepository.findById(3)).thenReturn(Optional.of(user));

        Optional<UserEntity> result = userRepository.getUserById(3);

        assertThat(result).containsSame(user);
    }

    @Test
    void updateUserStatusShouldSaveWhenUserExists() {
        UserEntity user = userEntity(4);
        when(userJpaRepository.findById(4)).thenReturn(Optional.of(user));
        when(userJpaRepository.save(user)).thenReturn(user);

        Optional<UserEntity> result = userRepository.updateUserStatus(4, UserEntity.Status.INACTIVE);

        assertThat(result).containsSame(user);
        assertThat(user.getStatus()).isEqualTo(UserEntity.Status.INACTIVE);
    }

    @Test
    void updateUserStatusShouldReturnEmptyWhenUserDoesNotExist() {
        when(userJpaRepository.findById(9)).thenReturn(Optional.empty());

        Optional<UserEntity> result = userRepository.updateUserStatus(9, UserEntity.Status.INACTIVE);

        assertThat(result).isEmpty();
    }
}
