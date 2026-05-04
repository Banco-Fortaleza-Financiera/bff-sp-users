package com.bancofortaleza.users.services;

import org.springframework.data.domain.Page;

import com.bff.services.server.models.AddressCreateRequest;
import com.bff.services.server.models.AddressResponse;
import com.bff.services.server.models.Status;

public interface AddressService {
    
   Page<AddressResponse> listUserAddresses(Integer userId, Integer xPage, Integer xPageSize, String search, Status status);

   AddressResponse createUserAddress(Integer userId,AddressCreateRequest userCreateRequest);

   AddressResponse getUserAddressById(Integer userId, Integer addressId);

    AddressResponse updateUserAddressStatus(Integer userId, Integer addressId, Status status);
    
}
