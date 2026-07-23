package com.guvaren.securityjwt.master.fakultas.controller;

import com.guvaren.securityjwt.base.Response;
import com.guvaren.securityjwt.master.fakultas.model.FakultasReq;
import com.guvaren.securityjwt.master.fakultas.model.FakultasRes;
import com.guvaren.securityjwt.master.fakultas.service.FakultasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fakultas")
public class FakultasController {
    private final FakultasService fakultasService;

    @GetMapping
    @PreAuthorize("hasAuthority('fakultas:read')")
    public ResponseEntity<Response<List<FakultasRes>>> get() {
        List<FakultasRes> result = this.fakultasService.get();
        return ResponseEntity.ok(Response.success(result));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('fakultas:read')")
    public ResponseEntity<Response<Optional<FakultasRes>>> get(@PathVariable String id) {
        Optional<FakultasRes> result = this.fakultasService.getById(id);
        return ResponseEntity.ok(Response.success(result));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('fakultas:create')")
    public ResponseEntity<Response<Optional<FakultasRes>>> post(@RequestBody FakultasReq req) {
        Optional<FakultasRes> result = this.fakultasService.save(req);
        return ResponseEntity.ok(Response.created(result));
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('fakultas:update')")
    public ResponseEntity<Response<Optional<FakultasRes>>> put(@PathVariable String id, @RequestBody FakultasReq req) {
        Optional<FakultasRes> result = this.fakultasService.update(req, id);
        return ResponseEntity.ok(Response.updated(result));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('fakultas:update')")
    public ResponseEntity<Response<Optional<FakultasRes>>> delete(@PathVariable String id) {
        Optional<FakultasRes> result = this.fakultasService.delete(id);
        return ResponseEntity.ok(Response.deleted(result));
    }
}
