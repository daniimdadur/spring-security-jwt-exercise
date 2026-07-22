package com.guvaren.securityjwt.base;

import com.guvaren.securityjwt.master.auth.entity.RoleEntity;
import com.guvaren.securityjwt.master.auth.entity.UserEntity;
import com.guvaren.securityjwt.master.auth.enums.Roles;
import com.guvaren.securityjwt.master.auth.repository.RoleRepo;
import com.guvaren.securityjwt.master.auth.repository.UserRepo;
import com.guvaren.securityjwt.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String @NonNull ... args) throws Exception {
        RoleEntity superAdmin = this.roleRepo.findByRole(Roles.SUPER_ADMIN)
                .orElseGet(() -> this.roleRepo.save(
                        RoleEntity.builder()
                                .id(CommonUtil.getUUID())
                                .role(Roles.SUPER_ADMIN)
                                .build()
                ));
        RoleEntity admin = this.roleRepo.findByRole(Roles.ADMIN)
                .orElseGet(() -> this.roleRepo.save(
                        RoleEntity.builder()
                                .id(CommonUtil.getUUID())
                                .role(Roles.ADMIN)
                                .build()
                ));
        RoleEntity userEntity = this.roleRepo.findByRole(Roles.USER)
                .orElseGet(() -> this.roleRepo.save(
                        RoleEntity.builder()
                                .id(CommonUtil.getUUID())
                                .role(Roles.USER)
                                .build()
                ));

        if (!this.userRepo.existsByEmail("superadmin@guvaren.com")) {
            UserEntity user = UserEntity.builder()
                    .id(CommonUtil.getUUID())
                    .firstName("super")
                    .lastName("admin")
                    .email("superadmin@guvaren.com")
                    .password(this.passwordEncoder.encode("superadmin"))
                    .roles(Set.of(superAdmin, admin))
                    .build();
            this.userRepo.save(user);
        }

        if (!this.userRepo.existsByEmail("admin@guvaren.com")) {
            UserEntity user = UserEntity.builder()
                    .id(CommonUtil.getUUID())
                    .firstName("admin")
                    .lastName("admin")
                    .email("admin@guvaren.com")
                    .password(this.passwordEncoder.encode("admin"))
                    .roles(Set.of(admin))
                    .build();
            this.userRepo.save(user);
        }

        if (!this.userRepo.existsByEmail("user@guvaren.com")) {
            UserEntity user = UserEntity.builder()
                    .id(CommonUtil.getUUID())
                    .firstName("user")
                    .lastName("user")
                    .email("user@guvaren.com")
                    .password(this.passwordEncoder.encode("user"))
                    .roles(Set.of(userEntity))
                    .build();
            this.userRepo.save(user);
        }
    }
}
