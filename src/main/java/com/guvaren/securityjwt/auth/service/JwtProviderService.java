package com.guvaren.securityjwt.auth.service;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class JwtProviderService {
    private final String secret;
    private final SecretKey secretKey;
    private final long jwtAccessExpiration;
    private final long jwtRefreshExpiration;

    public JwtProviderService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-expiration:60}") long accessExpirationMinutes,
            @Value("${app.jwt.refresh-expiration:1440}") long refreshExpirationMinutes) {

        this.secret = secret;
        this.secretKey = generateSigningKey();
        this.jwtAccessExpiration = TimeUnit.MINUTES.toMillis(accessExpirationMinutes);
        this.jwtRefreshExpiration = TimeUnit.MINUTES.toMillis(refreshExpirationMinutes);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, "access", jwtAccessExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, "refresh", jwtRefreshExpiration);
    }

    public String extractUsername(String jwt) {
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

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return claims != null && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private SecretKey generateSigningKey() {
        byte[] decodedKey = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String buildToken(Map<String, Object> extraClaims,
                              UserDetails userDetails,
                              String type,
                              long expiration) {
        extraClaims.put("type", type);

        Instant now = Instant.now();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiration)))
                .signWith(secretKey)
                .compact();
    }
}