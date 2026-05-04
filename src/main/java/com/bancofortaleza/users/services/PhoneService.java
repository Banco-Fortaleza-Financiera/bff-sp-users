package com.bancofortaleza.users.services;

import com.bff.services.server.models.PhoneCreateRequest;
import com.bff.services.server.models.PhoneResponse;
import com.bff.services.server.models.Status;
import org.springframework.data.domain.Page;

public interface PhoneService {

   Page<PhoneResponse> listUserPhones(Integer userId, Integer xPage, Integer xPageSize, String search, Status status);

   PhoneResponse createUserPhone(Integer userId, PhoneCreateRequest phoneCreateRequest);

   PhoneResponse getUserPhoneById(Integer userId, Integer phoneId);

   PhoneResponse updateUserPhoneStatus(Integer userId, Integer phoneId, Status status);
}
