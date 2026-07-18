package com.guvaren.securityjwt.master.auth.repository;

import com.guvaren.securityjwt.master.auth.entity.RefreshTokenEntity;
import com.guvaren.securityjwt.master.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByToken(String token);
    List<RefreshTokenEntity> findAllByUserAndRevokedFalse(UserEntity user);
    @Modifying
    @Query("update RefreshTokenEntity r set r.revoked = true, r.expired = true where r.user.email = :email")
    void revokeAllUserTokens(@Param("email") String email);
    boolean existsByToken(String token);
}
