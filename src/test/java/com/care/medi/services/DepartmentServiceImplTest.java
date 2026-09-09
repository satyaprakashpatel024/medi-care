package com.care.medi.services;

import com.care.medi.dtos.request.DepartmentRequestDTO;
import com.care.medi.dtos.response.DepartmentResponseDTO;
import com.care.medi.entity.Department;
import com.care.medi.exception.DuplicateResourceException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DepartmentService Unit Tests")
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department testDepartment;
    private DepartmentRequestDTO departmentRequestDTO;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
                .name("Cardiology")
                .description("Heart related diseases")
                .build();
        testDepartment.setId(1L);

        departmentRequestDTO = new DepartmentRequestDTO();
        departmentRequestDTO.setName("Cardiology");
        departmentRequestDTO.setDescription("Heart related diseases");
    }

    @Test
    @DisplayName("Should get all departments with pagination")
    void testGetAllDepartments() {
        Page<Department> page = new PageImpl<>(List.of(testDepartment));
        when(departmentRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<DepartmentResponseDTO> response = departmentService.getAllDepartments(0, 10, "id");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Cardiology", response.getContent().get(0).name());
        verify(departmentRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should create department successfully")
    void testCreateDepartment() {
        when(departmentRepository.existsByName("Cardiology")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(testDepartment);

        DepartmentResponseDTO response = departmentService.createDepartment(departmentRequestDTO);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Cardiology", response.name());
        verify(departmentRepository).existsByName("Cardiology");
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when creating existing department")
    void testCreateDepartment_Duplicate() {
        when(departmentRepository.existsByName("Cardiology")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> departmentService.createDepartment(departmentRequestDTO));
        verify(departmentRepository).existsByName("Cardiology");
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    @DisplayName("Should return department by id")
    void testGetDepartmentById() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));

        DepartmentResponseDTO response = departmentService.getDepartmentById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Cardiology", response.name());
        verify(departmentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when department not found by id")
    void testGetDepartmentById_NotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentService.getDepartmentById(1L));
        verify(departmentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should update department successfully")
    void testUpdateDepartment() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(departmentRepository.saveAndFlush(any(Department.class))).thenReturn(testDepartment);

        DepartmentRequestDTO updateRequest = new DepartmentRequestDTO();
        updateRequest.setName("Neurology");
        updateRequest.setDescription("Brain related diseases");

        DepartmentResponseDTO response = departmentService.updateDepartment(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Neurology", testDepartment.getName());
        assertEquals("Brain related diseases", testDepartment.getDescription());
        verify(departmentRepository).findById(1L);
        verify(departmentRepository).saveAndFlush(testDepartment);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent department")
    void testUpdateDepartment_NotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentService.updateDepartment(1L, departmentRequestDTO));
        verify(departmentRepository).findById(1L);
        verify(departmentRepository, never()).saveAndFlush(any(Department.class));
    }
}
