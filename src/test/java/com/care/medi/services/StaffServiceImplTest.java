package com.care.medi.services;

import com.care.medi.dtos.request.StaffRequestDTO;
import com.care.medi.dtos.request.StaffUpdateRequestDTO;
import com.care.medi.dtos.response.StaffResponseDTO;
import com.care.medi.entity.Hospital;
import com.care.medi.entity.Staff;
import com.care.medi.entity.Users;
import com.care.medi.exception.DuplicateResourceException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.HospitalRepository;
import com.care.medi.repository.StaffRepository;
import com.care.medi.repository.UsersRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StaffService Unit Tests")
class StaffServiceImplTest {

    @Mock
    private StaffRepository staffRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private StaffServiceImpl staffService;

    private Staff testStaff;
    private Users testUser;
    private Hospital testHospital;
    private StaffRequestDTO requestDTO;
    private StaffUpdateRequestDTO updateRequestDTO;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(1L);
        testUser.setEmail("staff@test.com");

        testHospital = new Hospital();
        testHospital.setId(1L);
        testHospital.setName("City Hospital");

        testStaff = Staff.builder()
                .user(testUser)
                .firstName("John")
                .lastName("Doe")
                .phone("1234567890")
                .hospital(testHospital)
                .build();
        testStaff.setId(1L);

        requestDTO = new StaffRequestDTO();
        requestDTO.setEmail("staff@test.com");
        requestDTO.setFirstName("John");
        requestDTO.setLastName("Doe");

        updateRequestDTO = new StaffUpdateRequestDTO();
        updateRequestDTO.setFirstName("Jane");
        updateRequestDTO.setPhone("0987654321");
    }

    @Test
    @DisplayName("Should create staff successfully")
    void testCreateStaff() {
        when(usersRepository.existsByEmail("staff@test.com")).thenReturn(false);
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(usersRepository.save(any(Users.class))).thenReturn(testUser);
        when(staffRepository.save(any(Staff.class))).thenReturn(testStaff);

        StaffResponseDTO response = staffService.createStaff(1L, requestDTO);

        assertNotNull(response);
        assertEquals("John", response.firstName());
        verify(usersRepository).existsByEmail("staff@test.com");
        verify(hospitalRepository).findById(1L);
        verify(usersRepository).save(any(Users.class));
        verify(staffRepository).save(any(Staff.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email exists")
    void testCreateStaff_DuplicateEmail() {
        when(usersRepository.existsByEmail("staff@test.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> staffService.createStaff(1L, requestDTO));
        verify(usersRepository).existsByEmail("staff@test.com");
        verify(hospitalRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should get all staff with pagination")
    void testGetAllStaff() {
        Page<Staff> page = new PageImpl<>(List.of(testStaff));
        when(staffRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<StaffResponseDTO> response = staffService.getAllStaff(0, 10, "id");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("John", response.getContent().get(0).firstName());
    }

    @Test
    @DisplayName("Should get staff by hospital")
    void testGetStaffByHospital() {
        when(hospitalRepository.existsById(1L)).thenReturn(true);
        Page<Staff> page = new PageImpl<>(List.of(testStaff));
        when(staffRepository.findByHospitalId(eq(1L), any(Pageable.class))).thenReturn(page);

        Page<StaffResponseDTO> response = staffService.getStaffByHospital(1L, 0, 10, "id");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("City Hospital", response.getContent().get(0).hospitalName());
        verify(hospitalRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid hospital in getStaffByHospital")
    void testGetStaffByHospital_NotFound() {
        when(hospitalRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> staffService.getStaffByHospital(1L, 0, 10, "id"));
    }

    @Test
    @DisplayName("Should get staff by ID")
    void testGetStaffById() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));

        StaffResponseDTO response = staffService.getStaffById(1L);

        assertNotNull(response);
        assertEquals("John", response.firstName());
        verify(staffRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid ID")
    void testGetStaffById_NotFound() {
        when(staffRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> staffService.getStaffById(1L));
    }

    @Test
    @DisplayName("Should update staff successfully")
    void testUpdateStaff() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));
        when(staffRepository.save(any(Staff.class))).thenReturn(testStaff);

        StaffResponseDTO response = staffService.updateStaff(1L, updateRequestDTO);

        assertNotNull(response);
        assertEquals("Jane", testStaff.getFirstName());
        assertEquals("0987654321", testStaff.getPhone());
        verify(staffRepository).findById(1L);
        verify(staffRepository).save(testStaff);
    }

    @Test
    @DisplayName("Should delete staff and related user")
    void testDeleteStaff() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));

        staffService.deleteStaff(1L);

        verify(staffRepository).findById(1L);
        verify(staffRepository).delete(testStaff);
        verify(usersRepository).delete(testUser);
    }
}
