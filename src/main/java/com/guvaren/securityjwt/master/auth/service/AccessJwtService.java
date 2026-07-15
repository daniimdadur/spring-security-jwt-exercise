package com.guvaren.securityjwt.master.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AccessJwtService {
    private final CustomUserDetailsService userDetailsService;
    private final String accessSecret;
    private final SecretKey accessSecretKey;
    private final long jwtAccessExpiration;

    public AccessJwtService(
            CustomUserDetailsService userDetailsService,
            @Value("${app.jwt.access-secret}") String accessSecret,
            @Value("${app.jwt.access-expiration:60}") long accessExpirationMinutes) {

        this.userDetailsService = userDetailsService;
        this.accessSecret = accessSecret;
        this.accessSecretKey = generateAccessSigningKey();
        this.jwtAccessExpiration = TimeUnit.MINUTES.toMillis(accessExpirationMinutes);
    }

    public String generateAccessToken(String email) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
        Map<String, Object> claims = Map.of("type", "access");
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(this.jwtAccessExpiration)))
                .signWith(this.accessSecretKey)
                .compact();
    }

    public String extractAccessUsername(String jwt) {
        Claims claims = getClaims(jwt);
        return claims != null ? claims.getSubject() : null;
    }

    public long expiredAt(String jwt) {
        try {
            Claims claims = getClaims(jwt);
            if (claims == null) return 0;

            long nowMillis = Instant.now().toEpochMilli();
            long expirationMillis = claims.getExpiration().getTime();

            return Math.max(0, (expirationMillis - nowMillis) / 1000 / 60);
        } catch (ExpiredJwtException e) {
            return 0;
        }
    }

    public boolean isAccessTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return claims != null && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private SecretKey generateAccessSigningKey() {
        byte[] decodedKey = this.accessSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.accessSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}