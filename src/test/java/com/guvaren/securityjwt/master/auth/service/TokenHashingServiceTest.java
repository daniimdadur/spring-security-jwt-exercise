package com.guvaren.securityjwt.master.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class TokenHashingServiceTest {

    private TokenHashingService tokenHashingService;

    @BeforeEach
    void setUp() {
        tokenHashingService = new TokenHashingService();
    }

    @Test
    void hashToken_shouldReturnNonEmptyString() {
        String hash = tokenHashingService.hashToken("test-token");
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    void hashToken_shouldReturnBase64String() {
        String hash = tokenHashingService.hashToken("test-token");
        assertDoesNotThrow(() -> Base64.getDecoder().decode(hash));
    }

    @Test
    void hashToken_shouldBeConsistent() {
        String hash1 = tokenHashingService.hashToken("same-token");
        String hash2 = tokenHashingService.hashToken("same-token");
        assertEquals(hash1, hash2);
    }

    @Test
    void hashToken_shouldProduceDifferentHashesForDifferentInputs() {
        String hash1 = tokenHashingService.hashToken("token-1");
        String hash2 = tokenHashingService.hashToken("token-2");
        assertNotEquals(hash1, hash2);
    }

    @Test
    void verifyToken_shouldReturnTrueForMatchingToken() {
        String rawToken = "my-secret-token";
        String hashed = tokenHashingService.hashToken(rawToken);
        assertTrue(tokenHashingService.verifyToken(rawToken, hashed));
    }

    @Test
    void verifyToken_shouldReturnFalseForNonMatchingToken() {
        String hashed = tokenHashingService.hashToken("correct-token");
        assertFalse(tokenHashingService.verifyToken("wrong-token", hashed));
    }

    @Test
    void hashToken_shouldHandleEmptyString() {
        String hash = tokenHashingService.hashToken("");
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    void hashToken_shouldHandleSpecialCharacters() {
        String hash = tokenHashingService.hashToken("t0k3n!@#$%^&*()_+-=");
        assertNotNull(hash);
    }

    @Test
    void hashToken_shouldProduce256BitHash() {
        String hash = tokenHashingService.hashToken("test");
        byte[] decoded = Base64.getDecoder().decode(hash);
        assertEquals(32, decoded.length, "SHA-256 produces 32 bytes");
    }
}
