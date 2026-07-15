package com.guvaren.securityjwt.master.auth.repository;

import com.guvaren.securityjwt.master.auth.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByToken(String token);
}
