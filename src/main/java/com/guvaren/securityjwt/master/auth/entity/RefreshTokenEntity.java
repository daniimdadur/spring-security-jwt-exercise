package com.guvaren.securityjwt.master.auth.entity;

import com.guvaren.securityjwt.master.auth.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_token")
public class RefreshTokenEntity {

    @Id
    @Column(name = "tid", length = 36)
    public String id;

    @Column(name = "token", unique = true)
    public String token;

    @Column(name = "expired")
    public LocalDateTime expired;

    @Column(name = "revoked")
    public boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public UserEntity user;
}
