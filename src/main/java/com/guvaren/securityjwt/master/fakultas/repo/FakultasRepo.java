package com.guvaren.securityjwt.master.fakultas.repo;

import com.guvaren.securityjwt.master.fakultas.model.FakultasEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FakultasRepo extends JpaRepository<FakultasEntity, String> {
    @Modifying
    @Transactional
    @Query(value = "delete from t_fakultas where deleted_at is not null", nativeQuery = true)
    int permanentDeleteSoftDeleted();
}
