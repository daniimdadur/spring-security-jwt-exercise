package com.guvaren.securityjwt.master.fakultas.service;

import com.guvaren.securityjwt.exception.NotFoundException;
import com.guvaren.securityjwt.master.fakultas.model.FakultasEntity;
import com.guvaren.securityjwt.master.fakultas.model.FakultasReq;
import com.guvaren.securityjwt.master.fakultas.model.FakultasRes;
import com.guvaren.securityjwt.master.fakultas.repo.FakultasRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FakultasServiceImplTest {

    @Mock
    private FakultasRepo fakultasRepo;

    @InjectMocks
    private FakultasServiceImpl fakultasService;

    private FakultasEntity testFakultas;

    @BeforeEach
    void setUp() {
        testFakultas = new FakultasEntity();
        testFakultas.setId("f1");
        testFakultas.setCode("FTI");
        testFakultas.setName("Fakultas Teknologi Informasi");
    }

    @Test
    void get_shouldReturnListOfFakultas() {
        when(fakultasRepo.findAll()).thenReturn(List.of(testFakultas));

        List<FakultasRes> result = fakultasService.get();

        assertEquals(1, result.size());
        assertEquals("f1", result.get(0).getId());
        assertEquals("FTI", result.get(0).getCode());
        assertEquals("Fakultas Teknologi Informasi", result.get(0).getName());
    }

    @Test
    void get_shouldReturnEmptyListWhenNoData() {
        when(fakultasRepo.findAll()).thenReturn(Collections.emptyList());

        List<FakultasRes> result = fakultasService.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void get_shouldHandleMultipleEntries() {
        FakultasEntity fakultas2 = new FakultasEntity();
        fakultas2.setId("f2");
        fakultas2.setCode("FK");
        fakultas2.setName("Fakultas Kedokteran");

        when(fakultasRepo.findAll()).thenReturn(List.of(testFakultas, fakultas2));

        List<FakultasRes> result = fakultasService.get();

        assertEquals(2, result.size());
    }

    @Test
    void get_withPageable_shouldReturnPage() {
        Page<FakultasEntity> page = new PageImpl<>(List.of(testFakultas), PageRequest.of(0, 20), 1);
        when(fakultasRepo.findAll(any(Pageable.class))).thenReturn(page);

        Page<FakultasRes> result = fakultasService.get(PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        assertEquals("FTI", result.getContent().get(0).getCode());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getById_shouldReturnFakultas() {
        when(fakultasRepo.findById("f1")).thenReturn(Optional.of(testFakultas));

        FakultasRes result = fakultasService.getById("f1");

        assertNotNull(result);
        assertEquals("f1", result.getId());
        assertEquals("FTI", result.getCode());
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(fakultasRepo.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> fakultasService.getById("nonexistent"));
    }

    @Test
    void save_shouldCreateNewFakultas() {
        FakultasReq req = new FakultasReq();
        req.setCode("FK");
        req.setName("Fakultas Kedokteran");

        when(fakultasRepo.save(any())).thenReturn(testFakultas);

        FakultasRes result = fakultasService.save(req);

        assertNotNull(result);
        verify(fakultasRepo).save(any());
    }

    @Test
    void update_shouldUpdateExistingFakultas() {
        when(fakultasRepo.findById("f1")).thenReturn(Optional.of(testFakultas));
        when(fakultasRepo.save(any())).thenReturn(testFakultas);

        FakultasReq req = new FakultasReq();
        req.setCode("FTI-NEW");
        req.setName("Fakultas Teknologi Informasi Updated");

        FakultasRes result = fakultasService.update(req, "f1");

        assertNotNull(result);
        verify(fakultasRepo).save(any());
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        when(fakultasRepo.findById("nonexistent")).thenReturn(Optional.empty());

        FakultasReq req = new FakultasReq();
        req.setCode("FK");

        assertThrows(NotFoundException.class,
                () -> fakultasService.update(req, "nonexistent"));
    }

    @Test
    void delete_shouldDeleteFakultas() {
        when(fakultasRepo.findById("f1")).thenReturn(Optional.of(testFakultas));

        FakultasRes result = fakultasService.delete("f1");

        assertNotNull(result);
        verify(fakultasRepo).delete(testFakultas);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(fakultasRepo.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> fakultasService.delete("nonexistent"));
    }
}
