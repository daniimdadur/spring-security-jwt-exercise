package com.guvaren.securityjwt.master.fakultas.controller;

import com.guvaren.securityjwt.master.fakultas.model.FakultasReq;
import com.guvaren.securityjwt.master.fakultas.model.FakultasRes;
import com.guvaren.securityjwt.master.fakultas.service.FakultasService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FakultasControllerTest {

    @Mock
    private FakultasService fakultasService;

    @InjectMocks
    private FakultasController fakultasController;

    @Test
    void get_shouldReturnListOfFakultas() {
        FakultasRes fakultasRes = new FakultasRes();
        fakultasRes.setId("f1");
        fakultasRes.setCode("FTI");
        fakultasRes.setName("Fakultas Teknologi Informasi");

        when(fakultasService.get()).thenReturn(List.of(fakultasRes));

        ResponseEntity result = fakultasController.get();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(fakultasService).get();
    }

    @Test
    void getById_shouldReturnFakultas() {
        FakultasRes fakultasRes = new FakultasRes();
        fakultasRes.setId("f1");
        fakultasRes.setCode("FTI");
        fakultasRes.setName("Fakultas Teknologi Informasi");

        when(fakultasService.getById("f1")).thenReturn(Optional.of(fakultasRes));

        ResponseEntity result = fakultasController.get("f1");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(fakultasService).getById("f1");
    }

    @Test
    void post_shouldReturnCreated() {
        FakultasReq req = new FakultasReq();
        req.setCode("FK");
        req.setName("Fakultas Kedokteran");

        when(fakultasService.save(req)).thenReturn(Optional.empty());

        ResponseEntity result = fakultasController.post(req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(fakultasService).save(req);
    }

    @Test
    void put_shouldReturnUpdated() {
        FakultasReq req = new FakultasReq();
        req.setCode("FTI-NEW");
        req.setName("Fakultas Teknologi Informasi Updated");

        when(fakultasService.update(req, "f1")).thenReturn(Optional.empty());

        ResponseEntity result = fakultasController.put("f1", req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(fakultasService).update(req, "f1");
    }

    @Test
    void delete_shouldReturnDeleted() {
        when(fakultasService.delete("f1")).thenReturn(Optional.empty());

        ResponseEntity result = fakultasController.delete("f1");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(fakultasService).delete("f1");
    }
}
