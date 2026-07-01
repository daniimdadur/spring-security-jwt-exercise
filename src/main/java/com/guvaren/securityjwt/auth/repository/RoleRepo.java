package com.guvaren.securityjwt.auth.repository;

import com.guvaren.securityjwt.auth.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<RoleEntity, String> {
}
