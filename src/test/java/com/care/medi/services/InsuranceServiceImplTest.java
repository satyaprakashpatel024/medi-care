package com.care.medi.services;

import com.care.medi.dtos.request.InsuranceRequestDTO;
import com.care.medi.dtos.response.InsuranceResponseDTO;
import com.care.medi.entity.Insurance;
import com.care.medi.repository.InsuranceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InsuranceService Unit Tests")
class InsuranceServiceImplTest {

    @Mock
    private InsuranceRepository insuranceRepository;

    @InjectMocks
    private InsuranceServiceImpl insuranceService;

    private Insurance testInsurance;
    private InsuranceRequestDTO insuranceRequestDTO;

    @BeforeEach
    void setUp() {
        testInsurance = new Insurance();
        testInsurance.setId(1L);
        testInsurance.setPolicyNumber("POL-12345");
        testInsurance.setProviderName("Test Insurance Provider");
        testInsurance.setCoverageAmount(100000.0);

        insuranceRequestDTO = new InsuranceRequestDTO();
        insuranceRequestDTO.setPolicyNumber("POL-12345");
        insuranceRequestDTO.setProviderName("Test Insurance Provider");
        insuranceRequestDTO.setCoverageAmount(100000.0);
    }

    @Test
    @DisplayName("Should get insurance by policy number successfully")
    void testGetInsuranceByPolicyNumber_Success() {
        when(insuranceRepository.findByPolicyNumber("POL-12345"))
                .thenReturn(Optional.of(testInsurance));

        InsuranceResponseDTO result = insuranceService.getInsuranceByPolicyNumber("POL-12345");

        assertNotNull(result);
        verify(insuranceRepository).findByPolicyNumber("POL-12345");
    }

    @Test
    @DisplayName("Should return null when insurance not found by policy number")
    void testGetInsuranceByPolicyNumber_NotFound() {
        when(insuranceRepository.findByPolicyNumber("INVALID-POL"))
                .thenReturn(Optional.empty());

        InsuranceResponseDTO result = insuranceService.getInsuranceByPolicyNumber("INVALID-POL");

        assertNull(result);
        verify(insuranceRepository).findByPolicyNumber("INVALID-POL");
    }

    @Test
    @DisplayName("Should create insurance - currently returns null")
    void testCreateInsurance() {
        InsuranceResponseDTO result = insuranceService.createInsurance(insuranceRequestDTO);

        assertNull(result);
    }

    @Test
    @DisplayName("Should update insurance - currently returns null")
    void testUpdateInsurance() {
        InsuranceResponseDTO result = insuranceService.updateInsurance(1L, insuranceRequestDTO);

        assertNull(result);
    }

    @Test
    @DisplayName("Should get insurance by hospital ID - currently returns null")
    void testGetInsuranceByHospitalId() {
        InsuranceResponseDTO result = insuranceService.getInsuranceByHospitalId(1L);

        assertNull(result);
    }

    @Test
    @DisplayName("Should get insurance by user ID - currently returns null")
    void testGetInsuranceByUserId() {
        InsuranceResponseDTO result = insuranceService.getInsuranceByUserId(1L);

        assertNull(result);
    }

    @Test
    @DisplayName("Should throw NotImplementedException when deleting insurance")
    void testDeleteInsurance_ThrowsException() {
        assertThrows(org.apache.commons.lang3.NotImplementedException.class,
                () -> insuranceService.deleteInsurance(1L));
    }
}
