package com.bancofortaleza.users.controller;

import static com.bancofortaleza.users.TestFixtures.addressCreateRequest;
import static com.bancofortaleza.users.TestFixtures.addressResponse;
import static com.bancofortaleza.users.TestFixtures.phoneCreateRequest;
import static com.bancofortaleza.users.TestFixtures.phoneResponse;
import static com.bancofortaleza.users.TestFixtures.userCreateRequest;
import static com.bancofortaleza.users.TestFixtures.userResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.bancofortaleza.users.services.AddressService;
import com.bancofortaleza.users.services.PhoneService;
import com.bancofortaleza.users.services.UsersService;
import com.bff.services.server.models.AddressResponse;
import com.bff.services.server.models.PhoneResponse;
import com.bff.services.server.models.Status;
import com.bff.services.server.models.StatusUpdateRequest;
import com.bff.services.server.models.UserResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    @Mock
    private UsersService usersService;

    @Mock
    private AddressService addressService;

    @Mock
    private PhoneService phoneService;

    @InjectMocks
    private UsersController usersController;

    @Test
    void createUserShouldReturnServiceResponse() {
        UserResponse response = userResponse(1);
        when(usersService.createUser(userCreateRequest())).thenReturn(response);

        ResponseEntity<UserResponse> result = usersController.createUser("ip", "session", 99, userCreateRequest());

        assertThat(result.getBody()).isSameAs(response);
    }

    @Test
    void listUsersShouldReturnPaginationHeadersAndContent() {
        UserResponse response = userResponse(1);
        when(usersService.listUsers(2, 1, "ger", Status.ACTIVE, com.bff.services.server.models.UserType.ADMIN))
                .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(1, 1), 3));

        ResponseEntity<List<UserResponse>> result = usersController.listUsers(
                "ip", "session", 99, 2, 1, "ger", Status.ACTIVE, com.bff.services.server.models.UserType.ADMIN);

        assertThat(result.getBody()).containsExactly(response);
        assertThat(result.getHeaders().getFirst("x-total-count")).isEqualTo("3");
        assertThat(result.getHeaders().getFirst("x-page")).isEqualTo("2");
        assertThat(result.getHeaders().getFirst("x-page-size")).isEqualTo("1");
        assertThat(result.getHeaders().getFirst("x-total-pages")).isEqualTo("3");
    }

    @Test
    void addressEndpointsShouldDelegateToAddressService() {
        AddressResponse response = addressResponse(10, 1);
        when(addressService.createUserAddress(1, addressCreateRequest())).thenReturn(response);
        when(addressService.getUserAddressById(1, 10)).thenReturn(response);
        when(addressService.updateUserAddressStatus(1, 10, Status.INACTIVE)).thenReturn(response);
        when(addressService.listUserAddresses(1, 1, 20, "", Status.ACTIVE))
                .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1));

        assertThat(usersController.createUserAddress("ip", "session", 99, 1, addressCreateRequest()).getBody())
                .isSameAs(response);
        assertThat(usersController.getUserAddressById("ip", "session", 99, 1, 10).getBody())
                .isSameAs(response);
        assertThat(usersController.updateUserAddressStatus("ip", "session", 99, 1, 10,
                new StatusUpdateRequest().status(Status.INACTIVE)).getBody()).isSameAs(response);
        ResponseEntity<List<AddressResponse>> listResult = usersController.listUserAddresses(
                "ip", "session", 99, 1, 1, 20, "", Status.ACTIVE);
        assertThat(listResult.getBody()).containsExactly(response);
        assertThat(listResult.getHeaders().getFirst("x-total-count")).isEqualTo("1");
    }

    @Test
    void phoneEndpointsShouldDelegateToPhoneService() {
        PhoneResponse response = phoneResponse(20, 1);
        when(phoneService.createUserPhone(1, phoneCreateRequest())).thenReturn(response);
        when(phoneService.getUserPhoneById(1, 20)).thenReturn(response);
        when(phoneService.updateUserPhoneStatus(1, 20, Status.INACTIVE)).thenReturn(response);
        when(phoneService.listUserPhones(1, 1, 20, "", Status.ACTIVE))
                .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1));

        assertThat(usersController.createUserPhone("ip", "session", 99, 1, phoneCreateRequest()).getBody())
                .isSameAs(response);
        assertThat(usersController.getUserPhoneById("ip", "session", 99, 1, 20).getBody())
                .isSameAs(response);
        assertThat(usersController.updateUserPhoneStatus("ip", "session", 99, 1, 20,
                new StatusUpdateRequest().status(Status.INACTIVE)).getBody()).isSameAs(response);
        ResponseEntity<List<PhoneResponse>> listResult = usersController.listUserPhones(
                "ip", "session", 99, 1, 1, 20, "", Status.ACTIVE);
        assertThat(listResult.getBody()).containsExactly(response);
        assertThat(listResult.getHeaders().getFirst("x-total-pages")).isEqualTo("1");
    }

    @Test
    void getAndUpdateUserShouldDelegateToUsersService() {
        UserResponse response = userResponse(1);
        when(usersService.getUserById(1)).thenReturn(response);
        when(usersService.updateUserStatus(1, Status.INACTIVE)).thenReturn(response);

        assertThat(usersController.getUserById("ip", "session", 99, 1).getBody()).isSameAs(response);
        assertThat(usersController.updateUserStatus("ip", "session", 99, 1,
                new StatusUpdateRequest().status(Status.INACTIVE)).getBody()).isSameAs(response);
    }
}
