package com.guvaren.securityjwt.auth.repository;

import com.guvaren.securityjwt.auth.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepo extends JpaRepository<TokenEntity, String> {
}
