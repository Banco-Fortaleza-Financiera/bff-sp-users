package com.bancofortaleza.users.services;

import com.bff.services.server.models.Status;
import com.bff.services.server.models.UserCreateRequest;
import com.bff.services.server.models.UserResponse;
import com.bff.services.server.models.UserType;

import org.springframework.data.domain.Page;

public interface UsersService {
    
   Page<UserResponse> listUsers(Integer xPage, Integer xPageSize, String search, Status status, UserType userType);

   UserResponse createUser(UserCreateRequest userCreateRequest);

   UserResponse getUserById(int userId);

   UserResponse updateUserStatus(Integer id, Status status);
}
