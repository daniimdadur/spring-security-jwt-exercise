package com.guvaren.securityjwt.auth.repository;

import com.guvaren.securityjwt.auth.entity.RoleEntity;
import com.guvaren.securityjwt.auth.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepo extends JpaRepository<RoleEntity, String> {
    Optional<RoleEntity> findByRole(Roles role);
    Set<RoleEntity> findByRoleIn(Set<Roles> roles);
}
