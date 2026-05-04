package com.bancofortaleza.users.services.impl;

import com.bancofortaleza.users.repository.users.UserRepository;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bancofortaleza.users.services.BCryptPasswordHashService;
import com.bancofortaleza.users.services.UserValidationService;
import com.bancofortaleza.users.services.UsersService;
import com.bancofortaleza.users.services.mapper.UserMapper;
import com.bff.services.server.models.Status;
import com.bff.services.server.models.UserCreateRequest;
import com.bff.services.server.models.UserResponse;
import com.bff.services.server.models.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordHashService passwordHashService;
    private final UserValidationService userValidationService;

    @Override
    @Cacheable(
            value = "users:list:page",
            key = "{#xPage, #xPageSize, #search, #status, #userType}"
    )
    @Transactional(readOnly = true)
    public Page<UserResponse> listUsers(Integer xPage, Integer xPageSize, String search, Status status, UserType userType) {
        UserEntity.Status statusEnum = userMapper.toEntityStatus(status);
        UserEntity.UserType userTypeEnum = userMapper.toEntityUserType(userType);

        return userRepository.listUsers(xPage, xPageSize, search, statusEnum, userTypeEnum)
                .map(userMapper::toUserResponse);
    }

    @Override
    @CacheEvict(value = {"users:list", "users:list:page", "users:detail"}, allEntries = true)
    @Transactional
    public UserResponse createUser(UserCreateRequest userCreateRequest) {
        UserEntity userEntity = userMapper.toUserCreateRequest(userCreateRequest);
        userEntity.setPassword(passwordHashService.hash(userEntity.getPassword()));
        userEntity = userRepository.createUser(userEntity);
        return userMapper.toUserResponse(userEntity);
    }

    @Override
    @Cacheable(value = "users:detail", key = "#userId")
    @Transactional(readOnly = true)
    public UserResponse getUserById(int userId) {
        UserEntity userEntity = userValidationService.getUserOrThrow(userId);
        return userMapper.toUserResponse(userEntity);
    }

    @Override
    @CacheEvict(value = {"users:list", "users:list:page", "users:detail"}, allEntries = true)
    @Transactional
    public UserResponse updateUserStatus(Integer id, Status status) {
        UserEntity.Status statusEnum = userMapper.toEntityStatus(status);
        UserEntity userEntity = userRepository.updateUserStatus(id, statusEnum)
                .orElseThrow(() -> userValidationService.userNotFoundException(id));
        return userMapper.toUserResponse(userEntity);
    }
}
