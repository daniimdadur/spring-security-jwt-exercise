package com.guvaren.securityjwt.master.auth.controller;

import com.guvaren.securityjwt.master.auth.dto.req.RolesReq;
import com.guvaren.securityjwt.master.auth.dto.res.UserRes;
import com.guvaren.securityjwt.master.auth.enums.Roles;
import com.guvaren.securityjwt.master.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiUserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private ApiUserController apiUserController;

    @Test
    void get_shouldReturnListOfUsers() {
        UserRes userRes = UserRes.builder()
                .id("u1")
                .firstName("Test")
                .lastName("User")
                .email("test@email.com")
                .roles(Set.of(Roles.USER))
                .build();

        when(userService.get()).thenReturn(List.of(userRes));

        ResponseEntity result = apiUserController.get();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userService).get();
    }

    @Test
    void updateNewRoles_shouldReturnUpdatedMessage() {
        RolesReq req = new RolesReq();
        req.setRoles(Set.of(Roles.ADMIN));

        when(userService.updateNewRoles(req, "u1")).thenReturn("User roles updated successfully");

        ResponseEntity result = apiUserController.updateNewRoles(req, "u1");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userService).updateNewRoles(req, "u1");
    }
}
