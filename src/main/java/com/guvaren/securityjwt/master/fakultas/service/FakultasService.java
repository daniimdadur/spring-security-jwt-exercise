package com.guvaren.securityjwt.master.fakultas.service;

import com.guvaren.securityjwt.master.fakultas.model.FakultasReq;
import com.guvaren.securityjwt.master.fakultas.model.FakultasRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FakultasService {
    List<FakultasRes> get();
    Page<FakultasRes> get(Pageable pageable);
    FakultasRes getById(String id);
    FakultasRes save(FakultasReq request);
    FakultasRes update(FakultasReq request, String id);
    FakultasRes delete(String id);
}
