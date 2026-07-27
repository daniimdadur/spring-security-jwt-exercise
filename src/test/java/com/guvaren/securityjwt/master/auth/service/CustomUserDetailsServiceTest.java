package com.guvaren.securityjwt.master.auth.service;

import com.guvaren.securityjwt.master.auth.entity.PermissionEntity;
import com.guvaren.securityjwt.master.auth.entity.RoleEntity;
import com.guvaren.securityjwt.master.auth.entity.UserEntity;
import com.guvaren.securityjwt.master.auth.enums.Permissions;
import com.guvaren.securityjwt.master.auth.enums.Roles;
import com.guvaren.securityjwt.master.auth.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        PermissionEntity readPermission = PermissionEntity.builder()
                .id("p1")
                .name(Permissions.FAKULTAS_READ)
                .build();

        RoleEntity userRole = RoleEntity.builder()
                .id("r1")
                .role(Roles.USER)
                .permissions(Set.of(readPermission))
                .build();

        testUser = UserEntity.builder()
                .id("u1")
                .email("test@email.com")
                .firstName("Test")
                .lastName("User")
                .password("encoded-password")
                .roles(new HashSet<>(Set.of(userRole)))
                .build();
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        when(userRepo.findByEmail("test@email.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@email.com");

        assertNotNull(userDetails);
        assertEquals("test@email.com", userDetails.getUsername());
        assertEquals("encoded-password", userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_shouldIncludeRoleAuthority() {
        when(userRepo.findByEmail("test@email.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@email.com");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_shouldIncludePermissionAuthority() {
        when(userRepo.findByEmail("test@email.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@email.com");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("fakultas:read")));
    }

    @Test
    void loadUserByUsername_shouldThrowWhenUserNotFound() {
        when(userRepo.findByEmail("missing@email.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("missing@email.com"));
    }

    @Test
    void loadUserByUsername_shouldHaveCorrectAuthorityCount() {
        when(userRepo.findByEmail("test@email.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@email.com");

        assertEquals(2, userDetails.getAuthorities().size());
    }
}
