package com.guvaren.securityjwt.master.fakultas.repo;

import com.guvaren.securityjwt.master.fakultas.model.FakultasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FakultasRepo extends JpaRepository<FakultasEntity, String> {
}
