package com.guvaren.securityjwt.master.auth.controller;

import com.guvaren.securityjwt.master.auth.dto.req.AuthenticationReq;
import com.guvaren.securityjwt.master.auth.dto.req.RegistrationReq;
import com.guvaren.securityjwt.master.auth.dto.res.AuthenticationResult;
import com.guvaren.securityjwt.master.auth.dto.res.TokenRes;
import com.guvaren.securityjwt.master.auth.service.AuthService;
import com.guvaren.securityjwt.util.CookieUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiAuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private CookieUtil cookieUtil;

    @InjectMocks
    private ApiAuthController apiAuthController;

    @Test
    void register_shouldReturnCreatedResponse() {
        RegistrationReq req = new RegistrationReq();
        req.setFirstName("Test");
        req.setLastName("User");
        req.setEmail("test@email.com");
        req.setPassword("password");

        AuthenticationResult authResult = AuthenticationResult.builder()
                .accessToken("access-token")
                .accessTokenExpiration(15L)
                .refreshToken("refresh-token")
                .refreshTokenExpiration(1440L)
                .build();

        when(authService.register(any())).thenReturn(authResult);

        jakarta.servlet.http.HttpServletResponse response = mock(jakarta.servlet.http.HttpServletResponse.class);

        ResponseEntity result = apiAuthController.register(req, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(cookieUtil).addRefreshTokenCookie(response, "refresh-token");
    }

    @Test
    void login_shouldReturnSuccessResponse() {
        AuthenticationReq req = new AuthenticationReq();
        req.setEmail("test@email.com");
        req.setPassword("password");

        AuthenticationResult authResult = AuthenticationResult.builder()
                .accessToken("access-token")
                .accessTokenExpiration(15L)
                .refreshToken("refresh-token")
                .refreshTokenExpiration(1440L)
                .build();

        when(authService.login(any())).thenReturn(authResult);

        jakarta.servlet.http.HttpServletResponse response = mock(jakarta.servlet.http.HttpServletResponse.class);

        ResponseEntity result = apiAuthController.login(req, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(cookieUtil).addRefreshTokenCookie(response, "refresh-token");
    }

    @Test
    void loginLogout_shouldReturnSuccessResponse() {
        AuthenticationReq req = new AuthenticationReq();
        req.setEmail("test@email.com");
        req.setPassword("password");

        AuthenticationResult authResult = AuthenticationResult.builder()
                .accessToken("access-token")
                .accessTokenExpiration(15L)
                .refreshToken("refresh-token")
                .refreshTokenExpiration(1440L)
                .build();

        when(authService.loginAndLogoutForAllDevices(any())).thenReturn(authResult);

        jakarta.servlet.http.HttpServletResponse response = mock(jakarta.servlet.http.HttpServletResponse.class);

        ResponseEntity result = apiAuthController.loginLogout(req, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(cookieUtil).addRefreshTokenCookie(response, "refresh-token");
    }

    @Test
    void refreshToken_shouldReturnNewAccessToken() {
        TokenRes tokenRes = TokenRes.builder()
                .accessToken("new-access-token")
                .accessTokenExpiration(15L)
                .build();

        when(authService.getNewAccessToken("refresh-token")).thenReturn(tokenRes);

        ResponseEntity result = apiAuthController.refreshToken("refresh-token");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void logout_shouldReturnSuccessAndDeleteCookie() {
        when(authService.logoutThisDevice("refresh-token")).thenReturn("Logout successful from this device");

        jakarta.servlet.http.HttpServletResponse response = mock(jakarta.servlet.http.HttpServletResponse.class);

        ResponseEntity result = apiAuthController.logout("refresh-token", response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(cookieUtil).deleteRefreshTokenCookie(response);
    }

    @Test
    void logoutAllDevices_shouldReturnSuccessAndDeleteCookie() {
        when(authService.logoutAllDevices("refresh-token")).thenReturn("Logout successful from all devices");

        jakarta.servlet.http.HttpServletResponse response = mock(jakarta.servlet.http.HttpServletResponse.class);

        ResponseEntity result = apiAuthController.logoutAllDevices("refresh-token", response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(cookieUtil).deleteRefreshTokenCookie(response);
    }
}
