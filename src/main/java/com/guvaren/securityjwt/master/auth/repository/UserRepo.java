package com.guvaren.securityjwt.master.auth.repository;

import com.guvaren.securityjwt.master.auth.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    @Modifying
    @Transactional
    @Query(value = "delete from t_user where deleted_at is not null", nativeQuery = true)
    int permanentDeleteSoftDeleted();
}
