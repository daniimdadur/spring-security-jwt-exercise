package com.guvaren.securityjwt.master.fakultas.service;

import com.guvaren.securityjwt.exception.NotFoundException;
import com.guvaren.securityjwt.master.fakultas.model.FakultasEntity;
import com.guvaren.securityjwt.master.fakultas.model.FakultasReq;
import com.guvaren.securityjwt.master.fakultas.model.FakultasRes;
import com.guvaren.securityjwt.master.fakultas.repo.FakultasRepo;
import com.guvaren.securityjwt.util.CommonUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FakultasServiceImpl implements FakultasService {
    private final FakultasRepo fakultasRepo;

    @Override
    public List<FakultasRes> get() {
        List<FakultasEntity> result = this.fakultasRepo.findAll();
        if (result.isEmpty()) {
            return Collections.emptyList();
        }

        return result.stream().map(this::convertEntityToRes).collect(Collectors.toList());
    }

    public Page<FakultasRes> get(Pageable pageable) {
        Page<FakultasEntity> result = this.fakultasRepo.findAll(pageable);
        return result.map(this::convertEntityToRes);
    }

    @Override
    public FakultasRes getById(String id) {
        FakultasEntity entity = this.fakultasRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Fakultas not found with id: " + id));
        return convertEntityToRes(entity);
    }

    @Override
    public FakultasRes save(FakultasReq request) {
        FakultasEntity entity = new FakultasEntity();
        entity.setId(CommonUtil.getUUID());
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        FakultasEntity saved = this.fakultasRepo.save(entity);
        return convertEntityToRes(saved);
    }

    @Override
    public FakultasRes update(FakultasReq request, String id) {
        FakultasEntity entity = this.fakultasRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Fakultas not found with id: " + id));
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        FakultasEntity updated = this.fakultasRepo.save(entity);
        return convertEntityToRes(updated);
    }

    @Override
    @Transactional
    public FakultasRes delete(String id) {
        FakultasEntity entity = this.fakultasRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Fakultas not found with id: " + id));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            entity.setDeletedBy(auth.getName());
            entity.setDeletedAt(LocalDateTime.now());
        }
        return convertEntityToRes(entity);
    }

    //@Scheduled(cron = "0 0 0 * * *") deleted at 12 pm everyday
    @Scheduled(fixedRate = 60000)
    public void cleanUp() {
        int count = this.fakultasRepo.deleteByDeletedAtIsNotNull();
        log.info("Deleted {} fakultas", count);
    }

    private FakultasRes convertEntityToRes(FakultasEntity entity) {
        FakultasRes result = new FakultasRes();
        BeanUtils.copyProperties(entity, result);
        return result;
    }
}
