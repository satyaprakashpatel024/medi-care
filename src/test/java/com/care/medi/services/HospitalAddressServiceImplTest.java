package com.care.medi.services;

import com.care.medi.dtos.request.HospitalAddressRequestDTO;
import com.care.medi.dtos.response.HospitalAddressResponseDTO;
import com.care.medi.entity.Hospital;
import com.care.medi.entity.HospitalAddress;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.HospitalAddressRepository;
import com.care.medi.repository.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HospitalAddressService Unit Tests")
class HospitalAddressServiceImplTest {

    @Mock
    private HospitalAddressRepository hospitalAddressRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private HospitalAddressServiceImpl hospitalAddressService;

    private Hospital testHospital;
    private HospitalAddress testHospitalAddress;
    private HospitalAddressRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        testHospital = new Hospital();
        testHospital.setId(1L);

        testHospitalAddress = new HospitalAddress();
        testHospitalAddress.setId(1L);
        testHospitalAddress.setHospitalId(1L);
        testHospitalAddress.setAddressLine1("123 Med St");
        testHospitalAddress.setCity("Metropolis");

        requestDTO = new HospitalAddressRequestDTO();
        requestDTO.setAddressLine1("123 Med St");
        requestDTO.setCity("Metropolis");
    }

    @Test
    @DisplayName("Should get hospital address by ID")
    void testGetHospitalAddressById() {
        when(hospitalAddressRepository.findById(1L)).thenReturn(Optional.of(testHospitalAddress));

        HospitalAddressResponseDTO response = hospitalAddressService.getHospitalAddressById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("123 Med St", response.addressLine1());
        assertEquals("Metropolis", response.city());
        verify(hospitalAddressRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid ID")
    void testGetHospitalAddressById_NotFound() {
        when(hospitalAddressRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hospitalAddressService.getHospitalAddressById(1L));
        verify(hospitalAddressRepository).findById(1L);
    }

    @Test
    @DisplayName("Should create hospital address")
    void testCreateHospitalAddress() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(hospitalAddressRepository.save(any(HospitalAddress.class))).thenReturn(testHospitalAddress);

        HospitalAddress response = hospitalAddressService.createHospitalAddress(1L, requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("123 Med St", response.getAddressLine1());
        verify(hospitalRepository).findById(1L);
        verify(hospitalAddressRepository).save(any(HospitalAddress.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when hospital not found")
    void testCreateHospitalAddress_HospitalNotFound() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hospitalAddressService.createHospitalAddress(1L, requestDTO));
        verify(hospitalRepository).findById(1L);
        verify(hospitalAddressRepository, never()).save(any(HospitalAddress.class));
    }
}
