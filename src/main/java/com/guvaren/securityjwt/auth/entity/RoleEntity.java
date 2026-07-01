package com.guvaren.securityjwt.auth.entity;

import com.guvaren.securityjwt.auth.enums.Roles;
import com.guvaren.securityjwt.util.CommonUtil;
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

    @PrePersist
    private void generateId() {
        if (this.id == null) {
            this.id = CommonUtil.getUUID();
        }
    }
}
