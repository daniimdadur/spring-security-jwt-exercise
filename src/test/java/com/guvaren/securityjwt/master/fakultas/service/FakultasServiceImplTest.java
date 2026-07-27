package com.guvaren.securityjwt.master.fakultas.service;

import com.guvaren.securityjwt.master.fakultas.model.FakultasEntity;
import com.guvaren.securityjwt.master.fakultas.model.FakultasRes;
import com.guvaren.securityjwt.master.fakultas.repo.FakultasRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    void getById_shouldReturnEmptyOptional() {
        Optional<FakultasRes> result = fakultasService.getById("f1");
        assertTrue(result.isEmpty());
    }

    @Test
    void save_shouldReturnEmptyOptional() {
        assertTrue(fakultasService.save(null).isEmpty());
    }

    @Test
    void update_shouldReturnEmptyOptional() {
        assertTrue(fakultasService.update(null, "f1").isEmpty());
    }

    @Test
    void delete_shouldReturnEmptyOptional() {
        assertTrue(fakultasService.delete("f1").isEmpty());
    }
}
