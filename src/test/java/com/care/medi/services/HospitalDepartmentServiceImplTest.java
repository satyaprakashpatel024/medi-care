package com.care.medi.services;

import com.care.medi.dtos.response.HospitalDepartmentResponseDTO;
import com.care.medi.entity.Department;
import com.care.medi.entity.Hospital;
import com.care.medi.entity.HospitalDepartment;
import com.care.medi.exception.DuplicateResourceException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.DepartmentRepository;
import com.care.medi.repository.HospitalDepartmentRepository;
import com.care.medi.repository.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HospitalDepartmentService Unit Tests")
class HospitalDepartmentServiceImplTest {

    @Mock
    private HospitalDepartmentRepository hospitalDepartmentRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private HospitalDepartmentServiceImpl hospitalDepartmentService;

    private Hospital testHospital;
    private Department testDepartment;
    private HospitalDepartment testHospitalDepartment;

    @BeforeEach
    void setUp() {
        testHospital = new Hospital();
        testHospital.setId(1L);

        testDepartment = new Department();
        testDepartment.setId(1L);

        testHospitalDepartment = new HospitalDepartment();
        testHospitalDepartment.setId(1L);
        testHospitalDepartment.setHospital(testHospital);
        testHospitalDepartment.setDepartment(testDepartment);
        testHospitalDepartment.setActive(true);
    }

    @Test
    @DisplayName("Should find all hospital departments")
    void testFindAll() {
        when(hospitalDepartmentRepository.findAll()).thenReturn(List.of(testHospitalDepartment));

        List<HospitalDepartmentResponseDTO> response = hospitalDepartmentService.findAll();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).id());
        verify(hospitalDepartmentRepository).findAll();
    }

    @Test
    @DisplayName("Should map department to hospital successfully")
    void testMapDepartmentToHospital() {
        when(hospitalDepartmentRepository.existsByHospitalIdAndDepartmentId(1L, 1L)).thenReturn(false);
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(hospitalDepartmentRepository.save(any(HospitalDepartment.class))).thenReturn(testHospitalDepartment);

        HospitalDepartmentResponseDTO response = hospitalDepartmentService.mapDepartmentToHospital(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        verify(hospitalDepartmentRepository).existsByHospitalIdAndDepartmentId(1L, 1L);
        verify(hospitalDepartmentRepository).save(any(HospitalDepartment.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when mapping already exists")
    void testMapDepartmentToHospital_Duplicate() {
        when(hospitalDepartmentRepository.existsByHospitalIdAndDepartmentId(1L, 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> hospitalDepartmentService.mapDepartmentToHospital(1L, 1L));
        verify(hospitalDepartmentRepository).existsByHospitalIdAndDepartmentId(1L, 1L);
        verify(hospitalDepartmentRepository, never()).save(any(HospitalDepartment.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when hospital not found during mapping")
    void testMapDepartmentToHospital_HospitalNotFound() {
        when(hospitalDepartmentRepository.existsByHospitalIdAndDepartmentId(1L, 1L)).thenReturn(false);
        when(hospitalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hospitalDepartmentService.mapDepartmentToHospital(1L, 1L));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when department not found during mapping")
    void testMapDepartmentToHospital_DepartmentNotFound() {
        when(hospitalDepartmentRepository.existsByHospitalIdAndDepartmentId(1L, 1L)).thenReturn(false);
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hospitalDepartmentService.mapDepartmentToHospital(1L, 1L));
    }

    @Test
    @DisplayName("Should unmap department from hospital successfully")
    void testUnmapDepartmentFromHospital() {
        when(hospitalDepartmentRepository.findByHospitalIdAndDepartmentId(1L, 1L)).thenReturn(Optional.of(testHospitalDepartment));

        hospitalDepartmentService.unmapDepartmentFromHospital(1L, 1L);

        verify(hospitalDepartmentRepository).findByHospitalIdAndDepartmentId(1L, 1L);
        verify(hospitalDepartmentRepository).delete(testHospitalDepartment);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when unmapping non-existent mapping")
    void testUnmapDepartmentFromHospital_NotFound() {
        when(hospitalDepartmentRepository.findByHospitalIdAndDepartmentId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hospitalDepartmentService.unmapDepartmentFromHospital(1L, 1L));
        verify(hospitalDepartmentRepository).findByHospitalIdAndDepartmentId(1L, 1L);
        verify(hospitalDepartmentRepository, never()).delete(any(HospitalDepartment.class));
    }
}
