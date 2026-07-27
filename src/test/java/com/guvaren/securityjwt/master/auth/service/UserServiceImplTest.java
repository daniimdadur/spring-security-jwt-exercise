package com.guvaren.securityjwt.master.auth.service;

import com.guvaren.securityjwt.exception.BadRequestException;
import com.guvaren.securityjwt.exception.NotFoundException;
import com.guvaren.securityjwt.master.auth.dto.req.RolesReq;
import com.guvaren.securityjwt.master.auth.dto.res.UserRes;
import com.guvaren.securityjwt.master.auth.entity.RoleEntity;
import com.guvaren.securityjwt.master.auth.entity.UserEntity;
import com.guvaren.securityjwt.master.auth.enums.Roles;
import com.guvaren.securityjwt.master.auth.repository.RoleRepo;
import com.guvaren.securityjwt.master.auth.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity testUser;
    private RoleEntity userRole;

    @BeforeEach
    void setUp() {
        userRole = RoleEntity.builder()
                .id("r1")
                .role(Roles.USER)
                .permissions(new HashSet<>())
                .build();

        testUser = UserEntity.builder()
                .id("u1")
                .email("test@email.com")
                .firstName("Test")
                .lastName("User")
                .password("encoded")
                .roles(new HashSet<>(Set.of(userRole)))
                .build();
    }

    @Test
    void get_shouldReturnListOfUsers() {
        when(userRepo.findAll()).thenReturn(List.of(testUser));

        List<UserRes> result = userService.get();

        assertEquals(1, result.size());
        assertEquals("test@email.com", result.get(0).getEmail());
        assertEquals("Test", result.get(0).getFirstName());
        assertEquals("User", result.get(0).getLastName());
    }

    @Test
    void get_shouldReturnEmptyListWhenNoUsers() {
        when(userRepo.findAll()).thenReturn(Collections.emptyList());

        List<UserRes> result = userService.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void get_shouldIncludeUserRoles() {
        when(userRepo.findAll()).thenReturn(List.of(testUser));

        List<UserRes> result = userService.get();

        assertTrue(result.get(0).getRoles().contains(Roles.USER));
    }

    @Test
    void updateNewRoles_shouldUpdateSuccessfully() {
        when(userRepo.findById("u1")).thenReturn(Optional.of(testUser));
        RoleEntity adminRole = RoleEntity.builder().id("r2").role(Roles.ADMIN).permissions(new HashSet<>()).build();
        when(roleRepo.findByRoleIn(Set.of(Roles.ADMIN))).thenReturn(Set.of(adminRole));

        RolesReq req = new RolesReq();
        req.setRoles(Set.of(Roles.ADMIN));

        String result = userService.updateNewRoles(req, "u1");

        assertEquals("User roles updated successfully", result);
    }

    @Test
    void updateNewRoles_shouldThrowWhenUserNotFound() {
        when(userRepo.findById("nonexistent")).thenReturn(Optional.empty());

        RolesReq req = new RolesReq();
        req.setRoles(Set.of(Roles.ADMIN));

        assertThrows(NotFoundException.class,
                () -> userService.updateNewRoles(req, "nonexistent"));
    }

    @Test
    void updateNewRoles_shouldThrowWhenRolesDoNotExist() {
        when(userRepo.findById("u1")).thenReturn(Optional.of(testUser));
        when(roleRepo.findByRoleIn(Set.of(Roles.ADMIN))).thenReturn(Set.of());

        RolesReq req = new RolesReq();
        req.setRoles(Set.of(Roles.ADMIN));

        assertThrows(BadRequestException.class,
                () -> userService.updateNewRoles(req, "u1"));
    }

    @Test
    void updateNewRoles_shouldHandleNullRoles() {
        when(userRepo.findById("u1")).thenReturn(Optional.of(testUser));

        RolesReq req = new RolesReq();
        req.setRoles(null);

        String result = userService.updateNewRoles(req, "u1");

        assertEquals("User roles updated successfully", result);
    }

    @Test
    void updateNewRoles_shouldHandleEmptyRoles() {
        when(userRepo.findById("u1")).thenReturn(Optional.of(testUser));

        RolesReq req = new RolesReq();
        req.setRoles(Set.of());

        String result = userService.updateNewRoles(req, "u1");

        assertEquals("User roles updated successfully", result);
    }
}
