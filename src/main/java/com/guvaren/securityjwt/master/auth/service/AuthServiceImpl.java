package com.guvaren.securityjwt.master.auth.service;

import com.guvaren.securityjwt.exception.DuplicateException;
import com.guvaren.securityjwt.exception.JwtAuthenticationException;
import com.guvaren.securityjwt.exception.NotFoundException;
import com.guvaren.securityjwt.master.auth.dto.req.AuthenticationReq;
import com.guvaren.securityjwt.master.auth.dto.req.RegistrationReq;
import com.guvaren.securityjwt.master.auth.dto.res.AuthenticationRes;
import com.guvaren.securityjwt.master.auth.dto.res.TokenRes;
import com.guvaren.securityjwt.master.auth.entity.RefreshTokenEntity;
import com.guvaren.securityjwt.master.auth.entity.RoleEntity;
import com.guvaren.securityjwt.master.auth.entity.UserEntity;
import com.guvaren.securityjwt.master.auth.enums.Roles;
import com.guvaren.securityjwt.master.auth.enums.TokenType;
import com.guvaren.securityjwt.master.auth.repository.RefreshTokenRepo;
import com.guvaren.securityjwt.master.auth.repository.RoleRepo;
import com.guvaren.securityjwt.master.auth.repository.UserRepo;
import com.guvaren.securityjwt.util.CommonUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final AccessJwtService accessJwtService;
    private final RefreshJwtService refreshJwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthenticationRes register(RegistrationReq req) {
        this.userRepo.findByEmail(req.getEmail()).ifPresent(duplicate -> {
            throw new DuplicateException("Email already exists");
        });
        UserEntity user = UserEntity.builder()
                .id(CommonUtil.getUUID())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();
        RoleEntity userRole = this.roleRepo.findByRole(Roles.USER)
                .orElseGet(() -> this.roleRepo.save(
                        RoleEntity.builder()
                                .id(CommonUtil.getUUID())
                                .role(Roles.USER)
                                .build()
                ));

        user.setRoles(Set.of(userRole));
        try {
            return generateAuthenticationRes(this.userRepo.save(user));
        } catch (Exception e) {
            throw new RuntimeException("error registering user: " + e.getMessage());
        }
    }

    @Override
    public AuthenticationRes login(AuthenticationReq req) {
        try {
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getEmail(),
                            req.getPassword()
                    )
            );

            UserEntity user = this.userRepo.findByEmail(req.getEmail())
                    .orElseThrow(() -> new NotFoundException("User not found with email: " + req.getEmail()));
            return generateAuthenticationRes(user);
        } catch (Exception e) {
            throw new RuntimeException("error occurred while logging in: " + e.getMessage());
        }
    }

    @Override
    public TokenRes getNewAccessToken(String refreshToken) {
        RefreshTokenEntity refreshTokenEntity = this.refreshTokenRepo.findByToken(refreshToken)
                .orElseThrow(() -> new JwtAuthenticationException("Refresh token not found"));

        if (refreshTokenEntity.isRevoked()) {
            throw new JwtAuthenticationException("Refresh token has been revoked");
        }

        if (refreshTokenEntity.isExpired()) {
            throw new JwtAuthenticationException("Refresh token has expired");
        }

        if (!this.refreshJwtService.isRefreshTokenValid(refreshToken)) {
            refreshTokenEntity.setExpired(true);
            refreshTokenEntity.setRevoked(true);
            this.refreshTokenRepo.save(refreshTokenEntity);
            throw new JwtAuthenticationException("Refresh token has expired or invalid");
        }

        try {
            String username = this.refreshJwtService.extractRefreshUsername(refreshToken);
            String accessToken = this.accessJwtService.generateAccessToken(username);
            return TokenRes.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiration(this.accessJwtService.getRemainingMinutes(accessToken))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error generating new access token: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) throw new JwtAuthenticationException("User is not authenticated");
        String email = authentication.getName();
        this.refreshTokenRepo.revokeAllUserTokens(email);
    }

    private AuthenticationRes generateAuthenticationRes(UserEntity user){
        String accessToken = this.accessJwtService.generateAccessToken(user.getEmail());
        String refreshToken = this.refreshJwtService.generateRefreshToken(user.getEmail());
        saveUserToken(user, refreshToken);
        return AuthenticationRes.builder()
                .accessToken(accessToken)
                .accessTokenExpiration(this.accessJwtService.getRemainingMinutes(accessToken))
                .refreshToken(refreshToken)
                .refreshTokenExpiration(this.refreshJwtService.getRemainingMinutes(refreshToken))
                .build();

    }

    private Set<RoleEntity> saveRoles(Set<Roles> roles) {
        Set<RoleEntity> roleEntities = new HashSet<>();
        if (roles == null || roles.isEmpty()) {
            roleEntities.add(
                    RoleEntity.builder()
                            .id(CommonUtil.getUUID())
                            .role(Roles.USER)
                    .build()
            );
        } else {
            roleEntities = roles.stream()
                    .map(role -> RoleEntity.builder()
                            .id(CommonUtil.getUUID())
                            .role(role)
                            .build())
                    .collect(Collectors.toSet());
        }

        try {
            this.roleRepo.saveAll(roleEntities);
            return roleEntities;
        } catch (Exception e) {
            log.error("Error saving roles: {}", e.getMessage());
            throw new RuntimeException("Error saving roles: " + e.getMessage());
        }
    }

    private void saveUserToken(UserEntity user, String refreshToken) {
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .id(CommonUtil.getUUID())
                .token(refreshToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .user(user)
                .build();
        try {
            this.refreshTokenRepo.save(refreshTokenEntity);
        } catch (Exception e) {
            log.error("Error saving user token: {}", e.getMessage());
            throw new RuntimeException("Error saving user token: " + e.getMessage());
        }
    }
}
