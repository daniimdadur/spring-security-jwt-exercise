package com.guvaren.securityjwt.auth.service;

import com.guvaren.securityjwt.auth.dto.req.AuthenticationReq;
import com.guvaren.securityjwt.auth.dto.req.RegistrationReq;
import com.guvaren.securityjwt.auth.dto.res.AuthenticationRes;
import com.guvaren.securityjwt.auth.repository.TokenRepo;
import com.guvaren.securityjwt.auth.repository.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;
    private final JwtProviderService jwtProviderService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepo userRepo, TokenRepo tokenRepo, JwtProviderService jwtProviderService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
        this.jwtProviderService = jwtProviderService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticationRes register(RegistrationReq req) {
        return null;
    }

    @Override
    public AuthenticationRes login(AuthenticationReq req) {
        return null;
    }

    @Override
    public String Logout(String refreshToken) {
        return "";
    }
}
