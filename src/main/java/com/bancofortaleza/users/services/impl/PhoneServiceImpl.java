package com.bancofortaleza.users.services.impl;

import com.bancofortaleza.users.repository.users.PhoneRepository;
import com.bancofortaleza.users.repository.users.entity.PhoneEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bancofortaleza.users.domain.exceptions.ApiException;
import com.bancofortaleza.users.services.PhoneService;
import com.bancofortaleza.users.services.UserValidationService;
import com.bancofortaleza.users.services.mapper.PhoneMapper;
import com.bff.services.server.models.PhoneCreateRequest;
import com.bff.services.server.models.PhoneResponse;
import com.bff.services.server.models.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PhoneServiceImpl implements PhoneService {

    private final PhoneRepository phoneRepository;
    private final PhoneMapper phoneMapper;
    private final UserValidationService userValidationService;

    @Override
    @Cacheable(
        value = "users:phones:list",
        key = "{#userId, #xPage, #xPageSize, #search, #status}"
    )
    @Transactional(readOnly = true)
    public Page<PhoneResponse> listUserPhones(Integer userId, Integer xPage, Integer xPageSize, String search, Status status) {
        userValidationService.getActiveUserOrThrow(
                userId,
                "Cannot list phones for an inactive user with id: " + userId
        );
        UserEntity.Status statusEnum = phoneMapper.toEntityStatus(status);

        return phoneRepository.listUserPhones(userId, xPage, xPageSize, search, statusEnum)
                .map(phoneMapper::toPhoneResponse);
    }

    @Override
    @CacheEvict(value = "users:phones:list", allEntries = true)
    @Transactional
    public PhoneResponse createUserPhone(Integer userId, PhoneCreateRequest phoneCreateRequest) {
        UserEntity user = userValidationService.getActiveUserOrThrow(
                userId,
                "Cannot add phone to an inactive user with id: " + userId
        );

        PhoneEntity phone = phoneMapper.toPhoneEntity(phoneCreateRequest);
        phone.setUser(user);
        phone = phoneRepository.createUserPhone(phone);

        return phoneMapper.toPhoneResponse(phone);
    }

    @Override
    @Cacheable(value = "users:phones:detail", key = "{#userId, #phoneId}", unless = "#result == null")
    @Transactional(readOnly = true)
    public PhoneResponse getUserPhoneById(Integer userId, Integer phoneId) {
        userValidationService.getActiveUserOrThrow(
                userId,
                "Cannot get phone for an inactive user with id: " + userId
        );
        PhoneEntity phone = getPhoneOrThrow(userId, phoneId);

        return phoneMapper.toPhoneResponse(phone);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users:phones:list", allEntries = true),
            @CacheEvict(value = "users:phones:detail", allEntries = true)
    })
    @Transactional
    public PhoneResponse updateUserPhoneStatus(Integer userId, Integer phoneId, Status status) {
        userValidationService.getActiveUserOrThrow(
                userId,
                "Cannot update phone for an inactive user with id: " + userId
        );
        PhoneEntity phone = getPhoneOrThrow(userId, phoneId);
        phone.setStatus(phoneMapper.toEntityStatus(status));
        phone = phoneRepository.updatePhoneStatus(userId, phoneId, phoneMapper.toEntityStatus(status));

        return phoneMapper.toPhoneResponse(phone);
    }

    private PhoneEntity getPhoneOrThrow(Integer userId, Integer phoneId) {
        PhoneEntity phone = phoneRepository.getUserPhoneById(userId, phoneId);
        if (phone == null) {
            throw new ApiException(
                    HttpStatus.NOT_FOUND,
                    "PHONE_NOT_FOUND",
                    "Phone not found with id: " + phoneId + " for user id: " + userId
            );
        }

        return phone;
    }
}
