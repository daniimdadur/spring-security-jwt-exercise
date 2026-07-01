package com.guvaren.securityjwt.auth.service;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class JwtProviderService {
    private static final String secretKey = "wdgFBVS9P2joCLJ3eNmknbg3IXlkZBpMOgdpriotWH0=";
    private static final long jwtAccessExpiration = TimeUnit.MINUTES.toMillis(60);
    private static final long jwtRefreshExpiration = TimeUnit.HOURS.toMillis(24);

    private Claims getClaims(String token) {
        return null;
    }
}
