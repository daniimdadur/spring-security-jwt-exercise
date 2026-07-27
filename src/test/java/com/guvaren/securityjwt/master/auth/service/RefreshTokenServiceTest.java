package com.guvaren.securityjwt.master.auth.service;

import com.guvaren.securityjwt.exception.BadRequestException;
import com.guvaren.securityjwt.exception.JwtAuthenticationException;
import com.guvaren.securityjwt.master.auth.entity.RefreshTokenEntity;
import com.guvaren.securityjwt.master.auth.entity.UserEntity;
import com.guvaren.securityjwt.master.auth.repository.RefreshTokenRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepo refreshTokenRepo;

    @Mock
    private TokenHashingService tokenHashingService;

    private RefreshTokenService refreshTokenService;

    private static final long TEST_EXPIRATION_MINUTES = 1440;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(
                TEST_EXPIRATION_MINUTES,
                refreshTokenRepo,
                tokenHashingService
        );

        testUser = UserEntity.builder()
                .id("u1")
                .email("test@email.com")
                .firstName("Test")
                .lastName("User")
                .password("encoded")
                .build();
    }

    @Test
    void generateRefreshToken_shouldReturnRawToken() {
        when(refreshTokenRepo.findAllByUserAndRevokedFalse(any())).thenReturn(Collections.emptyList());
        when(refreshTokenRepo.save(any())).thenReturn(null);
        when(tokenHashingService.hashToken(any())).thenReturn("hashed-token");

        String rawToken = refreshTokenService.generateRefreshToken(testUser);

        assertNotNull(rawToken);
        assertFalse(rawToken.isEmpty());
    }

    @Test
    void generateRefreshToken_shouldRevokeExistingTokens() {
        RefreshTokenEntity existingToken = new RefreshTokenEntity();
        existingToken.setRevoked(false);
        when(refreshTokenRepo.findAllByUserAndRevokedFalse(any())).thenReturn(List.of(existingToken));
        when(refreshTokenRepo.save(any())).thenReturn(null);
        when(tokenHashingService.hashToken(any())).thenReturn("hashed");

        refreshTokenService.generateRefreshToken(testUser);

        verify(refreshTokenRepo, times(1)).saveAll(any());
    }

    @Test
    void generateAdditionalRefreshToken_shouldNotRevokeExistingTokens() {
        when(refreshTokenRepo.save(any())).thenReturn(null);
        when(tokenHashingService.hashToken(any())).thenReturn("hashed");

        String rawToken = refreshTokenService.generateAdditionalRefreshToken(testUser);

        assertNotNull(rawToken);
        verify(refreshTokenRepo, never()).saveAll(any());
    }

    @Test
    void isRefreshTokenValid_shouldReturnTrueForValidToken() {
        RefreshTokenEntity validToken = new RefreshTokenEntity();
        validToken.setRevoked(false);
        validToken.setExpired(LocalDateTime.now().plusHours(1));
        when(tokenHashingService.hashToken("raw-token")).thenReturn("hashed-token");
        when(refreshTokenRepo.findByToken("hashed-token")).thenReturn(Optional.of(validToken));

        assertTrue(refreshTokenService.isRefreshTokenValid("raw-token"));
    }

    @Test
    void isRefreshTokenValid_shouldReturnFalseForRevokedToken() {
        RefreshTokenEntity revokedToken = new RefreshTokenEntity();
        revokedToken.setRevoked(true);
        revokedToken.setExpired(LocalDateTime.now().plusHours(1));
        when(tokenHashingService.hashToken("raw-token")).thenReturn("hashed-token");
        when(refreshTokenRepo.findByToken("hashed-token")).thenReturn(Optional.of(revokedToken));

        assertFalse(refreshTokenService.isRefreshTokenValid("raw-token"));
    }

    @Test
    void isRefreshTokenValid_shouldReturnFalseForExpiredToken() {
        RefreshTokenEntity expiredToken = new RefreshTokenEntity();
        expiredToken.setRevoked(false);
        expiredToken.setExpired(LocalDateTime.now().minusHours(1));
        when(tokenHashingService.hashToken("raw-token")).thenReturn("hashed-token");
        when(refreshTokenRepo.findByToken("hashed-token")).thenReturn(Optional.of(expiredToken));

        assertFalse(refreshTokenService.isRefreshTokenValid("raw-token"));
    }

    @Test
    void isRefreshTokenValid_shouldReturnFalseForNullToken() {
        assertFalse(refreshTokenService.isRefreshTokenValid(null));
    }

    @Test
    void isRefreshTokenValid_shouldReturnFalseForBlankToken() {
        assertFalse(refreshTokenService.isRefreshTokenValid("   "));
    }

    @Test
    void isRefreshTokenValid_shouldReturnFalseWhenTokenNotFound() {
        when(tokenHashingService.hashToken("unknown")).thenReturn("hashed-unknown");
        when(refreshTokenRepo.findByToken("hashed-unknown")).thenReturn(Optional.empty());

        assertFalse(refreshTokenService.isRefreshTokenValid("unknown"));
    }

    @Test
    void getValidRefreshToken_shouldThrowForNullToken() {
        assertThrows(BadRequestException.class,
                () -> refreshTokenService.getValidRefreshToken(null));
    }

    @Test
    void getValidRefreshToken_shouldThrowForBlankToken() {
        assertThrows(BadRequestException.class,
                () -> refreshTokenService.getValidRefreshToken("  "));
    }

    @Test
    void getValidRefreshToken_shouldThrowForInvalidToken() {
        when(tokenHashingService.hashToken("invalid")).thenReturn("hashed-invalid");
        when(refreshTokenRepo.findByToken("hashed-invalid")).thenReturn(Optional.empty());

        assertThrows(JwtAuthenticationException.class,
                () -> refreshTokenService.getValidRefreshToken("invalid"));
    }

    @Test
    void revokeRefreshToken_shouldSetRevokedTrue() {
        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setRevoked(false);
        token.setUser(testUser);
        when(tokenHashingService.hashToken("raw")).thenReturn("hashed");
        when(refreshTokenRepo.findByToken("hashed")).thenReturn(Optional.of(token));

        refreshTokenService.revokeRefreshToken("raw");

        assertTrue(token.isRevoked());
        verify(refreshTokenRepo).save(token);
    }

    @Test
    void revokeRefreshToken_shouldThrowForNullToken() {
        assertThrows(BadRequestException.class,
                () -> refreshTokenService.revokeRefreshToken(null));
    }

    @Test
    void revokeAllUserTokens_shouldRevokeAllTokens() {
        RefreshTokenEntity token1 = new RefreshTokenEntity();
        token1.setRevoked(false);
        RefreshTokenEntity token2 = new RefreshTokenEntity();
        token2.setRevoked(false);
        when(refreshTokenRepo.findAllByUserAndRevokedFalse(testUser)).thenReturn(List.of(token1, token2));

        refreshTokenService.revokeAllUserTokens(testUser);

        assertTrue(token1.isRevoked());
        assertTrue(token2.isRevoked());
        verify(refreshTokenRepo).saveAll(any());
    }

    @Test
    void revokeAllUserTokens_shouldNotSaveWhenNoTokens() {
        when(refreshTokenRepo.findAllByUserAndRevokedFalse(testUser)).thenReturn(Collections.emptyList());

        refreshTokenService.revokeAllUserTokens(testUser);

        verify(refreshTokenRepo, never()).saveAll(any());
    }

    @Test
    void cleanUpExpiredTokens_shouldDeleteExpiredTokens() {
        when(refreshTokenRepo.deleteByExpiredBefore(any())).thenReturn(5);

        refreshTokenService.cleanUpExpiredTokens();

        verify(refreshTokenRepo).deleteByExpiredBefore(any());
    }
}
