package com.guvaren.securityjwt.master.fakultas.controller;

import com.guvaren.securityjwt.base.BaseController;
import com.guvaren.securityjwt.base.Response;
import com.guvaren.securityjwt.master.fakultas.model.FakultasRes;
import com.guvaren.securityjwt.master.fakultas.service.FakultasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fakultas")
public class FakultasController extends BaseController<FakultasRes> {
    private final FakultasService fakultasService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Response> get() {
        List<FakultasRes> result = this.fakultasService.get();
        return getResponse(result);
    }
}
