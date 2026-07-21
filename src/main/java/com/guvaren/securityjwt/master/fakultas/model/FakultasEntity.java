package com.guvaren.securityjwt.master.fakultas.model;

import com.guvaren.securityjwt.base.BaseAuditableSoftDelete;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE t_fakultas SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Table(name = "t_fakultas")
public class FakultasEntity extends BaseAuditableSoftDelete {
    @Id
    @Column
    private String id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;
}
