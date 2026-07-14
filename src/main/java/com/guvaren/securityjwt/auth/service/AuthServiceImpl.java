package com.guvaren.securityjwt.auth.service;

import com.guvaren.securityjwt.auth.dto.req.AuthenticationReq;
import com.guvaren.securityjwt.auth.dto.req.RegistrationReq;
import com.guvaren.securityjwt.auth.dto.res.AuthenticationRes;
import com.guvaren.securityjwt.auth.dto.res.TokenRes;
import com.guvaren.securityjwt.auth.entity.RefreshTokenEntity;
import com.guvaren.securityjwt.auth.entity.RoleEntity;
import com.guvaren.securityjwt.auth.entity.UserEntity;
import com.guvaren.securityjwt.auth.enums.Roles;
import com.guvaren.securityjwt.auth.enums.TokenType;
import com.guvaren.securityjwt.auth.repository.RefreshTokenRepo;
import com.guvaren.securityjwt.auth.repository.RoleRepo;
import com.guvaren.securityjwt.auth.repository.UserRepo;
import com.guvaren.securityjwt.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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
    public AuthenticationRes register(RegistrationReq req) {
        this.userRepo.findByEmail(req.getEmail()).ifPresent(duplicate -> {
            throw new IllegalArgumentException("Email already exists");
        });
        UserEntity user = UserEntity.builder()
                .id(CommonUtil.getUUID())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();
        Set<RoleEntity> roles = this.roleRepo.findByRoleIn(req.getRoles());
        if (roles.isEmpty()) {
            roles = saveRoles(req.getRoles());
        }

        user.setRoles(roles);
        try {
            UserEntity userEntity = this.userRepo.save(user);
            String accessToken = this.accessJwtService.generateAccessToken(userEntity.getEmail());
            String refreshToken = this.refreshJwtService.generateRefreshToken(userEntity.getEmail());
            saveUserToken(userEntity, refreshToken);

            return AuthenticationRes.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiration(this.accessJwtService.expiredAt(accessToken))
                    .refreshToken(refreshToken)
                    .refreshTokenExpiration(this.refreshJwtService.expiredAt(refreshToken))
                    .build();
        } catch (Exception e) {
            log.error("Error saving user: {}", e.getMessage());
            throw new IllegalArgumentException("Error saving user: " + e.getMessage());
        }
    }

    @Override
    public AuthenticationRes login(AuthenticationReq req) {
        return null;
    }

    @Override
    public TokenRes getNewAccessToken(String refreshToken) {
        return null;
    }

    @Override
    public String Logout(String refreshToken) {
        return "";
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
            throw new IllegalArgumentException("Error saving roles: " + e.getMessage());
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
            throw new IllegalArgumentException("Error saving user token: " + e.getMessage());
        }
    }
}
