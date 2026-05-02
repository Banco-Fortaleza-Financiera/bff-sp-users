package com.bancofortaleza.users.services;

import com.bancofortaleza.users.domain.exceptions.ApiException;
import com.bancofortaleza.users.repository.users.UserRepository;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final UserRepository userRepository;

    public UserEntity getUserOrThrow(Integer userId) {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> userNotFoundException(userId));
    }

    public ApiException userNotFoundException(Integer userId) {
        return new ApiException(
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND",
                "User not found with id: " + userId
        );
    }

    public UserEntity getActiveUserOrThrow(Integer userId, String inactiveMessage) {
        UserEntity user = getUserOrThrow(userId);

        if (user.getStatus() == UserEntity.Status.INACTIVE) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "USER_INACTIVE",
                    inactiveMessage
            );
        }

        return user;
    }
}
