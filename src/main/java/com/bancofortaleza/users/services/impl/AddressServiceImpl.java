package com.bancofortaleza.users.services.impl;

import com.bancofortaleza.users.repository.users.AddressRepository;
import com.bancofortaleza.users.repository.users.entity.AddressEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bancofortaleza.users.services.AddressService;
import com.bancofortaleza.users.services.UserValidationService;
import com.bancofortaleza.users.services.mapper.AddressMapper;
import com.bff.services.server.models.AddressCreateRequest;
import com.bff.services.server.models.AddressResponse;
import com.bff.services.server.models.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserValidationService userValidationService;

    @Override
    @Cacheable(
        value = "users:addresses:list", 
        key = "{#userId, #xPage, #xPageSize, #search, #status}"
    )
    @Transactional(readOnly = true)
    public Page<AddressResponse> listUserAddresses(Integer userId, Integer xPage, Integer xPageSize, String search, Status status) {
        userValidationService.getActiveUserOrThrow(
                userId,
                "Cannot list addresses for an inactive user with id: " + userId
        );
        UserEntity.Status statusEnum = addressMapper.toEntityStatus(status);

        return addressRepository.listUserAddresses(userId, xPage, xPageSize, search, statusEnum)
                .map(addressMapper::toAddressResponse);
        
    }

    @Override
    @CacheEvict(value = "users:addresses:list", allEntries = true)
    @Transactional
    public AddressResponse createUserAddress(Integer userId, AddressCreateRequest addressCreateRequest) {
        UserEntity user = userValidationService.getActiveUserOrThrow(
                userId,
                "Cannot add address to an inactive user with id: " + userId
        );

        AddressEntity address = addressMapper.toAddressEntity(addressCreateRequest);
        address.setUser(user);
        address = addressRepository.createUserAddress(address);

        return addressMapper.toAddressResponse(address);
    }

    @Override
    @Cacheable(value = "users:addresses:detail", key = "{#userId, #addressId}")
    @Transactional(readOnly = true)
    public AddressResponse getUserAddressById(Integer userId, Integer addressId) {
        userValidationService.getActiveUserOrThrow(
                userId,
                "Cannot get address for an inactive user with id: " + userId
        );
        AddressEntity address = addressRepository.getUserAddressById(userId, addressId);

        return addressMapper.toAddressResponse(address);
    }

    @Override
    @CacheEvict(value = "users:addresses:detail", allEntries = true)
    @Transactional
    public AddressResponse updateUserAddressStatus(Integer userId, Integer addressId, Status status) {
        userValidationService.getActiveUserOrThrow(
                userId,
                "Cannot update address for an inactive user with id: " + userId
        );
        AddressEntity address = addressRepository.getUserAddressById(userId, addressId);
        address.setStatus(addressMapper.toEntityStatus(status));
        address = addressRepository.updateAddressStatus(userId, addressId, addressMapper.toEntityStatus(status));

        return addressMapper.toAddressResponse(address);
    }
}
