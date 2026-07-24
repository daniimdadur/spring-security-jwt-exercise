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
import com.guvaren.securityjwt.master.auth.repository.RoleRepo;
import com.guvaren.securityjwt.master.auth.repository.UserRepo;
import com.guvaren.securityjwt.util.CommonUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final AccessJwtService accessJwtService;
    private final RefreshTokenService refreshTokenService;
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
        RefreshTokenEntity refreshTokenEntity = this.refreshTokenService.getRefreshToken(refreshToken);

        if (refreshTokenEntity.isRevoked()) {
            throw new JwtAuthenticationException("Refresh token has been revoked");
        }

        if (!this.refreshTokenService.isRefreshTokenValid(refreshToken)) {
            this.refreshTokenService.revokeRefreshToken(refreshToken);
            throw new JwtAuthenticationException("Refresh token has expired or invalid");
        }

        try {
            String accessToken = this.accessJwtService.generateAccessToken(refreshTokenEntity.getUser().getEmail());
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
    public String logoutAllDevices(String refreshToken) {
        //this method used to logout user from all devices
//        String email = this.refreshTokenService.extractRefreshUsername(refreshToken);
//        this.refreshTokenRepo.revokeAllUserTokens(email);
        return "Logout successful from all devices";
    }

    @Override
    @Transactional
    public String logoutThisDevice(String refreshToken) {
//        RefreshTokenEntity refreshTokenEntity = this.refreshTokenRepo.findByToken(this.tokenHashingService.hashToken(refreshToken))
//                .orElse(null);
//        if (refreshTokenEntity != null) {
//            refreshTokenEntity.setRevoked(true);
//            refreshTokenEntity.setExpired(true);
//            this.refreshTokenRepo.save(refreshTokenEntity);
//        }
        return "Logout successful from this device";
    }

    private AuthenticationRes generateAuthenticationRes(UserEntity user){
        String accessToken = this.accessJwtService.generateAccessToken(user.getEmail());
        String refreshToken = this.refreshTokenService.generateRefreshToken(user);
        return AuthenticationRes.builder()
                .accessToken(accessToken)
                .accessTokenExpiration(this.accessJwtService.getRemainingMinutes(accessToken))
                .refreshToken(refreshToken)
                .refreshTokenExpiration(this.refreshTokenService.getRemainingMinutes(refreshToken))
                .build();

    }
}
