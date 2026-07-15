package com.guvaren.securityjwt.master.auth.entity;

import com.guvaren.securityjwt.master.auth.enums.Roles;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "t_roles")
public class RoleEntity {

    @Id
    @Column(name = "rid", length = 36)
    private String id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Roles role;
}
