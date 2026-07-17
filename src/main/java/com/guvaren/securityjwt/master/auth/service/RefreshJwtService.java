package com.guvaren.securityjwt.master.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshJwtService {
    private final CustomUserDetailsService userDetailsService;
    private final String refreshSecret;
    private final SecretKey refreshSecretKey;
    private final long jwtRefreshExpiration;

    public RefreshJwtService(
            CustomUserDetailsService userDetailsService,
            @Value("${app.jwt.refresh-secret}") String refreshSecret,
            @Value("${app.jwt.refresh-expiration:1440}") long jwtRefreshExpiration) {

        this.userDetailsService = userDetailsService;
        this.refreshSecret = refreshSecret;
        this.refreshSecretKey = generateRefreshSigningKey();
        this.jwtRefreshExpiration = TimeUnit.MINUTES.toMillis(jwtRefreshExpiration);
    }

    public String generateRefreshToken(String email) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
        Map<String, Object> claims = Map.of("type", "refresh");
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(this.jwtRefreshExpiration)))
                .signWith(this.refreshSecretKey)
                .compact();
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return claims != null && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractRefreshUsername(String jwt) {
        Claims claims = getClaims(jwt);
        return claims != null ? claims.getSubject() : null;
    }

    public long getRemainingMinutes(String jwt) {
        try {
            Claims claims = getClaims(jwt);
            if (claims == null || claims.getExpiration() == null) {
                return 0;
            }

            Instant now = Instant.now();
            Instant expiration = claims.getExpiration().toInstant();
            if (expiration.isBefore(now)) {
                return 0;
            }

            return ChronoUnit.MINUTES.between(now, expiration);
        } catch (JwtException | IllegalArgumentException e) {
            return 0;
        }
    }

    private SecretKey generateRefreshSigningKey() {
        byte[] decodedKey = this.refreshSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.refreshSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
