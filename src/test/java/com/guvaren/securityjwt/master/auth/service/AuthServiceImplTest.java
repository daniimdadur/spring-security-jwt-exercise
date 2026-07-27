package com.guvaren.securityjwt.master.auth.service;

import com.guvaren.securityjwt.exception.DuplicateException;
import com.guvaren.securityjwt.exception.NotFoundException;
import com.guvaren.securityjwt.master.auth.dto.req.AuthenticationReq;
import com.guvaren.securityjwt.master.auth.dto.req.RegistrationReq;
import com.guvaren.securityjwt.master.auth.dto.res.AuthenticationResult;
import com.guvaren.securityjwt.master.auth.dto.res.TokenRes;
import com.guvaren.securityjwt.master.auth.entity.RefreshTokenEntity;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private AccessJwtService accessJwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserEntity testUser;
    private RoleEntity userRole;

    @BeforeEach
    void setUp() {
        userRole = RoleEntity.builder()
                .id("r1")
                .role(Roles.USER)
                .permissions(Set.of())
                .build();

        testUser = UserEntity.builder()
                .id("u1")
                .email("test@email.com")
                .firstName("Test")
                .lastName("User")
                .password("encoded-password")
                .roles(Set.of(userRole))
                .build();
    }

    @Test
    void register_shouldCreateNewUser() {
        RegistrationReq req = RegistrationReq.builder()
                .firstName("Test")
                .lastName("User")
                .email("new@email.com")
                .password("password")
                .build();

        when(userRepo.findByEmail("new@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(roleRepo.findByRole(Roles.USER)).thenReturn(Optional.of(userRole));
        when(userRepo.save(any())).thenReturn(testUser);
        when(accessJwtService.generateAccessToken(any())).thenReturn("access-token");
        when(accessJwtService.getRemainingMinutes(any())).thenReturn(15L);
        when(refreshTokenService.generateAdditionalRefreshToken(any())).thenReturn("refresh-token");
        when(refreshTokenService.getRemainingMinutes(any())).thenReturn(1440L);

        AuthenticationResult result = authService.register(req);

        assertNotNull(result);
        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
        verify(userRepo).save(any());
    }

    @Test
    void register_shouldThrowDuplicateExceptionForExistingEmail() {
        RegistrationReq req = RegistrationReq.builder()
                .email("existing@email.com")
                .password("password")
                .build();

        when(userRepo.findByEmail("existing@email.com")).thenReturn(Optional.of(testUser));

        assertThrows(DuplicateException.class, () -> authService.register(req));
    }

    @Test
    void login_shouldAuthenticateAndReturnTokens() {
        AuthenticationReq req = AuthenticationReq.builder()
                .email("test@email.com")
                .password("password")
                .build();

        when(userRepo.findByEmail("test@email.com")).thenReturn(Optional.of(testUser));
        when(accessJwtService.generateAccessToken("test@email.com")).thenReturn("access-token");
        when(accessJwtService.getRemainingMinutes("access-token")).thenReturn(15L);
        when(refreshTokenService.generateAdditionalRefreshToken(testUser)).thenReturn("refresh-token");
        when(refreshTokenService.getRemainingMinutes("refresh-token")).thenReturn(1440L);

        AuthenticationResult result = authService.login(req);

        assertNotNull(result);
        assertEquals("access-token", result.getAccessToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        AuthenticationReq req = AuthenticationReq.builder()
                .email("missing@email.com")
                .password("password")
                .build();

        when(userRepo.findByEmail("missing@email.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.login(req));
    }

    @Test
    void loginAndLogoutForAllDevices_shouldRevokeOldTokens() {
        AuthenticationReq req = AuthenticationReq.builder()
                .email("test@email.com")
                .password("password")
                .build();

        when(userRepo.findByEmail("test@email.com")).thenReturn(Optional.of(testUser));
        when(accessJwtService.generateAccessToken("test@email.com")).thenReturn("access-token");
        when(accessJwtService.getRemainingMinutes("access-token")).thenReturn(15L);
        when(refreshTokenService.generateRefreshToken(testUser)).thenReturn("refresh-token");
        when(refreshTokenService.getRemainingMinutes("refresh-token")).thenReturn(1440L);

        AuthenticationResult result = authService.loginAndLogoutForAllDevices(req);

        assertNotNull(result);
        verify(refreshTokenService).generateRefreshToken(testUser);
    }

    @Test
    void getNewAccessToken_shouldReturnNewToken() {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUser(testUser);

        when(refreshTokenService.getValidRefreshToken("refresh-token")).thenReturn(refreshTokenEntity);
        when(accessJwtService.generateAccessToken("test@email.com")).thenReturn("new-access-token");
        when(accessJwtService.getRemainingMinutes("new-access-token")).thenReturn(15L);

        TokenRes result = authService.getNewAccessToken("refresh-token");

        assertEquals("new-access-token", result.getAccessToken());
        assertEquals(15L, result.getAccessTokenExpiration());
    }

    @Test
    void logoutAllDevices_shouldRevokeAllTokens() {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUser(testUser);

        when(refreshTokenService.getValidRefreshToken("refresh-token")).thenReturn(refreshTokenEntity);

        String result = authService.logoutAllDevices("refresh-token");

        assertEquals("Logout successful from all devices", result);
        verify(refreshTokenService).revokeAllUserTokens(testUser);
    }

    @Test
    void logoutThisDevice_shouldRevokeSingleToken() {
        String result = authService.logoutThisDevice("refresh-token");

        assertEquals("Logout successful from this device", result);
        verify(refreshTokenService).revokeRefreshToken("refresh-token");
    }
}
