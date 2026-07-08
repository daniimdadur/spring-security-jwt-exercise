package com.guvaren.securityjwt.auth.repository;

import com.guvaren.securityjwt.auth.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepo extends JpaRepository<TokenEntity, String> {
    Optional<TokenEntity> findByToken(String token);
}
