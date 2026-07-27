package com.guvaren.securityjwt.master.auth.service;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessJwtServiceTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    private AccessJwtService accessJwtService;

    private static final String TEST_SECRET = "wdgFBVS9P2joCLJ3eNmknbg3IXlkZBpMOgdpriotWH0=";
    private static final long TEST_EXPIRATION = 15;

    @BeforeEach
    void setUp() {
        accessJwtService = new AccessJwtService(userDetailsService, TEST_SECRET, TEST_EXPIRATION);
    }

    @Test
    void generateAccessToken_shouldReturnNonEmptyToken() {
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(new User("test@email.com", "password", Collections.emptyList()));

        String token = accessJwtService.generateAccessToken("test@email.com");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateAccessToken_shouldReturnThreePartJwt() {
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(new User("test@email.com", "password", Collections.emptyList()));

        String token = accessJwtService.generateAccessToken("test@email.com");
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have 3 parts: header.payload.signature");
    }

    @Test
    void extractAccessUsername_shouldReturnCorrectEmail() {
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(new User("user@test.com", "password", Collections.emptyList()));

        String token = accessJwtService.generateAccessToken("user@test.com");
        String username = accessJwtService.extractAccessUsername(token);
        assertEquals("user@test.com", username);
    }

    @Test
    void isAccessTokenValid_shouldReturnTrueForValidToken() {
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(new User("test@email.com", "password", Collections.emptyList()));

        String token = accessJwtService.generateAccessToken("test@email.com");
        assertTrue(accessJwtService.isAccessTokenValid(token));
    }

    @Test
    void isAccessTokenValid_shouldReturnFalseForInvalidToken() {
        assertFalse(accessJwtService.isAccessTokenValid("invalid.token.here"));
    }

    @Test
    void isAccessTokenValid_shouldReturnFalseForEmptyToken() {
        assertFalse(accessJwtService.isAccessTokenValid(""));
    }

    @Test
    void getRemainingMinutes_shouldReturnPositiveForValidToken() {
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(new User("test@email.com", "password", Collections.emptyList()));

        String token = accessJwtService.generateAccessToken("test@email.com");
        long remaining = accessJwtService.getRemainingMinutes(token);
        assertTrue(remaining > 0, "Remaining minutes should be positive for valid token");
        assertTrue(remaining <= 15, "Remaining minutes should not exceed configured expiration");
    }

    @Test
    void getRemainingMinutes_shouldReturnZeroForInvalidToken() {
        long remaining = accessJwtService.getRemainingMinutes("invalid.token.here");
        assertEquals(0, remaining);
    }

    @Test
    void extractAccessUsername_shouldThrowForInvalidToken() {
        assertThrows(JwtException.class,
                () -> accessJwtService.extractAccessUsername("invalid.token.here"));
    }
}
