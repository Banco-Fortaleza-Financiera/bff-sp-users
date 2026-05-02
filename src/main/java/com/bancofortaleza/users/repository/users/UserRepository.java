package com.bancofortaleza.users.repository.users;

import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bancofortaleza.users.repository.users.jpa.UserJpaRepository;
import com.bancofortaleza.users.utils.PaginationUtils;
import com.bancofortaleza.users.utils.SpecificationUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Transactional
    public UserEntity createUser(UserEntity user) {
        if (user.getAddresses() != null) {
            user.getAddresses().forEach(address -> address.setUser(user));
        }

        if (user.getPhones() != null) {
            user.getPhones().forEach(phone -> phone.setUser(user));
        }

        return userJpaRepository.save(user);
    }

    public Page<UserEntity> listUsers(Integer xPage, Integer xPageSize, String search, UserEntity.Status status, UserEntity.UserType userType) {
        Pageable pageable = PaginationUtils.fromHeaders(xPage, xPageSize, Sort.by(Sort.Direction.ASC, "id"));

        return userJpaRepository.findAll(buildListUsersSpecification(search, status, userType), pageable);
    }

    public Optional<UserEntity> getUserById(Integer id) {
        return userJpaRepository.findById(id);
    }

    @Transactional
    public Optional<UserEntity> updateUserStatus(Integer id, UserEntity.Status status) {
        return userJpaRepository.findById(id)
                .map(user -> {
                    user.setStatus(status);
                    return userJpaRepository.save(user);
                });
    }

    private Specification<UserEntity> buildListUsersSpecification(
            String search,
            UserEntity.Status status,
            UserEntity.UserType userType
    ) {
        return SpecificationUtils.<UserEntity>equalIfNotNull("status", status)
                .and(SpecificationUtils.equalIfNotNull("userType", userType))
                .and(SpecificationUtils.containsIgnoreCase(search, "name", "lastName"));
    }
}
