package com.bancofortaleza.users.controller;

import com.bff.services.server.SupportApi;
import com.bff.services.server.models.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bancofortaleza.users.security.AdminOnly;
import com.bancofortaleza.users.services.AddressService;
import com.bancofortaleza.users.services.PhoneService;
import com.bancofortaleza.users.services.UsersService;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class UsersController implements SupportApi {

    private final UsersService usersService;
    private final AddressService addressService;
    private final PhoneService phoneService;

    @Override
    @AdminOnly
    public ResponseEntity<UserResponse> createUser(String xDeviceIp, String xSession, Integer xUserid, UserCreateRequest userCreateRequest) {
        return ResponseEntity.ok(usersService.createUser(userCreateRequest));
    }

    @Override
    @AdminOnly
    public ResponseEntity<AddressResponse> createUserAddress(String xDeviceIp, String xSession, Integer xUserid, Integer id, AddressCreateRequest addressCreateRequest) {
        return ResponseEntity.ok(addressService.createUserAddress(id, addressCreateRequest));
    }

    @Override
    @AdminOnly
    public ResponseEntity<PhoneResponse> createUserPhone(String xDeviceIp, String xSession, Integer xUserid, Integer id, PhoneCreateRequest phoneCreateRequest) {
        return ResponseEntity.ok(phoneService.createUserPhone(id, phoneCreateRequest));
    }

    @Override
    @AdminOnly
    public ResponseEntity<AddressResponse> getUserAddressById(String xDeviceIp, String xSession, Integer xUserid, Integer id, Integer addressId) {
        return ResponseEntity.ok(addressService.getUserAddressById(id, addressId));
    }

    @Override
    @AdminOnly
    public ResponseEntity<UserResponse> getUserById(String xDeviceIp, String xSession, Integer xUserid, Integer id) {
        return ResponseEntity.ok(usersService.getUserById(id));
    }

    @Override
    @AdminOnly
    public ResponseEntity<PhoneResponse> getUserPhoneById(String xDeviceIp, String xSession, Integer xUserid, Integer id, Integer phoneId) {
        return ResponseEntity.ok(phoneService.getUserPhoneById(id, phoneId));
    }

    @Override
    @AdminOnly
    public ResponseEntity<List<AddressResponse>> listUserAddresses(String xDeviceIp, String xSession, Integer xUserid, Integer id, Integer xPage, Integer xPageSize, String search, Status status) {
        Page<AddressResponse> addresses = addressService.listUserAddresses(id, xPage, xPageSize, search, status);

        return ResponseEntity.ok()
                .header("x-total-count", String.valueOf(addresses.getTotalElements()))
                .header("x-page", String.valueOf(addresses.getNumber() + 1))
                .header("x-page-size", String.valueOf(addresses.getSize()))
                .header("x-total-pages", String.valueOf(addresses.getTotalPages()))
                .body(addresses.getContent());
    }

    @Override
    @AdminOnly
    public ResponseEntity<List<PhoneResponse>> listUserPhones(String xDeviceIp, String xSession, Integer xUserid, Integer id, Integer xPage, Integer xPageSize, String search, Status status) {
        Page<PhoneResponse> phones = phoneService.listUserPhones(id, xPage, xPageSize, search, status);

        return ResponseEntity.ok()
                .header("x-total-count", String.valueOf(phones.getTotalElements()))
                .header("x-page", String.valueOf(phones.getNumber() + 1))
                .header("x-page-size", String.valueOf(phones.getSize()))
                .header("x-total-pages", String.valueOf(phones.getTotalPages()))
                .body(phones.getContent());
    }

    @Override
    @AdminOnly
    public ResponseEntity<List<UserResponse>> listUsers(String xDeviceIp, String xSession, Integer xUserid, Integer xPage, Integer xPageSize, String search, Status status, UserType userType) {
        Page<UserResponse> users = usersService.listUsers(xPage, xPageSize, search, status, userType);

        return ResponseEntity.ok()
                .header("x-total-count", String.valueOf(users.getTotalElements()))
                .header("x-page", String.valueOf(users.getNumber() + 1))
                .header("x-page-size", String.valueOf(users.getSize()))
                .header("x-total-pages", String.valueOf(users.getTotalPages()))
                .body(users.getContent());
    }

    @Override
    @AdminOnly
    public ResponseEntity<AddressResponse> updateUserAddressStatus(String xDeviceIp, String xSession, Integer xUserid, Integer id, Integer addressId, StatusUpdateRequest statusUpdateRequest) {
        return ResponseEntity.ok(addressService.updateUserAddressStatus(id, addressId, statusUpdateRequest.getStatus()));
    }

    @Override
    @AdminOnly
    public ResponseEntity<PhoneResponse> updateUserPhoneStatus(String xDeviceIp, String xSession, Integer xUserid, Integer id, Integer phoneId, StatusUpdateRequest statusUpdateRequest) {
        return ResponseEntity.ok(phoneService.updateUserPhoneStatus(id, phoneId, statusUpdateRequest.getStatus()));
    }

    @Override
    @AdminOnly
    public ResponseEntity<UserResponse> updateUserStatus(String xDeviceIp, String xSession, Integer xUserid, Integer id, StatusUpdateRequest statusUpdateRequest) {
        return ResponseEntity.ok(usersService.updateUserStatus(id, statusUpdateRequest.getStatus()));
    }
}
