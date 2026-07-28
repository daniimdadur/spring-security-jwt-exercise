package com.guvaren.securityjwt.master.fakultas.controller;

import com.guvaren.securityjwt.master.fakultas.model.FakultasReq;
import com.guvaren.securityjwt.master.fakultas.model.FakultasRes;
import com.guvaren.securityjwt.master.fakultas.service.FakultasService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FakultasControllerTest {

    @Mock
    private FakultasService fakultasService;

    @InjectMocks
    private FakultasController fakultasController;

    @Test
    void get_shouldReturnPageOfFakultas() {
        FakultasRes fakultasRes = new FakultasRes();
        fakultasRes.setId("f1");
        fakultasRes.setCode("FTI");
        fakultasRes.setName("Fakultas Teknologi Informasi");

        Page<FakultasRes> page = new PageImpl<>(List.of(fakultasRes), PageRequest.of(0, 20), 1);
        when(fakultasService.get(any(Pageable.class))).thenReturn(page);

        ResponseEntity result = fakultasController.get(PageRequest.of(0, 20));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(fakultasService).get(any(Pageable.class));
    }

    @Test
    void getById_shouldReturnFakultas() {
        FakultasRes fakultasRes = new FakultasRes();
        fakultasRes.setId("f1");
        fakultasRes.setCode("FTI");
        fakultasRes.setName("Fakultas Teknologi Informasi");

        when(fakultasService.getById("f1")).thenReturn(fakultasRes);

        ResponseEntity result = fakultasController.get("f1");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(fakultasService).getById("f1");
    }

    @Test
    void post_shouldReturnCreated() {
        FakultasReq req = new FakultasReq();
        req.setCode("FK");
        req.setName("Fakultas Kedokteran");

        FakultasRes fakultasRes = new FakultasRes();
        fakultasRes.setId("f2");
        fakultasRes.setCode("FK");
        fakultasRes.setName("Fakultas Kedokteran");

        when(fakultasService.save(req)).thenReturn(fakultasRes);

        ResponseEntity result = fakultasController.post(req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(fakultasService).save(req);
    }

    @Test
    void put_shouldReturnUpdated() {
        FakultasReq req = new FakultasReq();
        req.setCode("FTI-NEW");
        req.setName("Fakultas Teknologi Informasi Updated");

        FakultasRes fakultasRes = new FakultasRes();
        fakultasRes.setId("f1");
        fakultasRes.setCode("FTI-NEW");
        fakultasRes.setName("Fakultas Teknologi Informasi Updated");

        when(fakultasService.update(req, "f1")).thenReturn(fakultasRes);

        ResponseEntity result = fakultasController.put("f1", req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(fakultasService).update(req, "f1");
    }

    @Test
    void delete_shouldReturnDeleted() {
        FakultasRes fakultasRes = new FakultasRes();
        fakultasRes.setId("f1");
        fakultasRes.setCode("FTI");
        fakultasRes.setName("Fakultas Teknologi Informasi");

        when(fakultasService.delete("f1")).thenReturn(fakultasRes);

        ResponseEntity result = fakultasController.delete("f1");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(fakultasService).delete("f1");
    }
}
