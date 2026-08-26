package com.care.medi.services;

import com.care.medi.dtos.request.HospitalAddressRequestDTO;
import com.care.medi.dtos.request.HospitalRequestDTO;
import com.care.medi.dtos.request.HospitalUpdateRequestDTO;
import com.care.medi.dtos.response.HospitalListResponseDTO;
import com.care.medi.dtos.response.HospitalResponseDTO;
import com.care.medi.entity.Department;
import com.care.medi.entity.Hospital;
import com.care.medi.entity.HospitalAddress;
import com.care.medi.entity.HospitalDepartment;
import com.care.medi.exception.BusinessException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HospitalService Unit Tests")
class HospitalServiceImplTest {

    @Mock
    private HospitalRepository hospitalRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private HospitalDepartmentRepository hospitalDepartmentRepository;
    @Mock
    private HospitalAddressServiceImpl hospitalAddressServiceImpl;

    @InjectMocks
    private HospitalServiceImpl hospitalService;

    private Hospital testHospital;
    private HospitalRequestDTO hospitalRequestDTO;
    private HospitalUpdateRequestDTO hospitalUpdateRequestDTO;
    private Department testDepartment;
    private HospitalAddress testAddress;

    @BeforeEach
    void setUp() {
        testHospital = new Hospital();
        testHospital.setId(1L);
        testHospital.setName("Test Hospital");
        testHospital.setPhone("1234567890");

        testAddress = new HospitalAddress();
        testAddress.setId(1L);
        testAddress.setAddressLine1("123 Main St");
        testAddress.setCity("Test City");
        testAddress.setState("Test State");
        testAddress.setPostalCode("12345");
        testAddress.setCountry("Test Country");
        testAddress.setPhoneNumber("1234567890");
        testAddress.setHospital(testHospital);
        testHospital.addAddress(testAddress);

        testDepartment = new Department();
        testDepartment.setId(1L);
        testDepartment.setName("Cardiology");

        HospitalAddressRequestDTO addressDTO = new HospitalAddressRequestDTO();
        addressDTO.setAddressLine1("123 Main St");
        addressDTO.setCity("Test City");
        addressDTO.setState("Test State");
        addressDTO.setPostalCode("12345");
        addressDTO.setCountry("Test Country");
        addressDTO.setPhoneNumber("1234567890");

        hospitalRequestDTO = new HospitalRequestDTO();
        hospitalRequestDTO.setName("Test Hospital");
        hospitalRequestDTO.setPhone("1234567890");
        hospitalRequestDTO.setAddress(addressDTO);

        hospitalUpdateRequestDTO = new HospitalUpdateRequestDTO();
        hospitalUpdateRequestDTO.setName("Updated Hospital");
        hospitalUpdateRequestDTO.setPhone("0987654321");
        hospitalUpdateRequestDTO.setAddress(addressDTO);
    }

    @Test
    @DisplayName("Should create hospital successfully")
    void testCreateHospital_Success() {
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(testHospital);

        HospitalResponseDTO result = hospitalService.createHospital(hospitalRequestDTO);

        assertNotNull(result);
        assertEquals("Test Hospital", result.name());
        verify(hospitalRepository).save(any(Hospital.class));
    }

    @Test
    @DisplayName("Should create hospital without address")
    void testCreateHospital_WithoutAddress_Success() {
        HospitalRequestDTO requestDTO = new HospitalRequestDTO();
        requestDTO.setName("Simple Hospital");
        requestDTO.setPhone("1234567890");
        requestDTO.setAddress(null);

        when(hospitalRepository.save(any(Hospital.class))).thenReturn(testHospital);

        HospitalResponseDTO result = hospitalService.createHospital(requestDTO);

        assertNotNull(result);
        verify(hospitalRepository).save(any(Hospital.class));
    }

    @Test
    @DisplayName("Should get hospital by ID successfully")
    void testGetHospitalById_Success() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));

        HospitalResponseDTO result = hospitalService.getHospitalById(1L);

        assertNotNull(result);
        assertEquals("Test Hospital", result.name());
        verify(hospitalRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when hospital not found by ID")
    void testGetHospitalById_NotFound() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hospitalService.getHospitalById(1L));

        verify(hospitalRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get all hospitals with pagination")
    void testGetAllHospitals_Success() {
        Page<Hospital> hospitalPage = new PageImpl<>(List.of(testHospital));
        when(hospitalRepository.findAll(any(Pageable.class))).thenReturn(hospitalPage);

        Page<HospitalListResponseDTO> result = hospitalService.getAllHospitals(0, 10, "id");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(hospitalRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should update hospital successfully")
    void testUpdateHospital_Success() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(hospitalAddressServiceImpl.createHospitalAddress(eq(1L), any(HospitalAddressRequestDTO.class)))
                .thenReturn(testAddress);
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(testHospital);

        HospitalResponseDTO result = hospitalService.updateHospital(1L, hospitalUpdateRequestDTO);

        assertNotNull(result);
        verify(hospitalRepository).findById(1L);
        verify(hospitalRepository).save(any(Hospital.class));
    }

    @Test
    @DisplayName("Should throw exception when hospital not found during update")
    void testUpdateHospital_NotFound() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> hospitalService.updateHospital(1L, hospitalUpdateRequestDTO));

        verify(hospitalRepository).findById(1L);
    }

    @Test
    @DisplayName("Should update only name")
    void testUpdateHospital_OnlyName() {
        HospitalUpdateRequestDTO updateDTO = new HospitalUpdateRequestDTO();
        updateDTO.setName("New Name");
        updateDTO.setPhone(null);
        updateDTO.setAddress(null);

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(testHospital);

        HospitalResponseDTO result = hospitalService.updateHospital(1L, updateDTO);

        assertNotNull(result);
        verify(hospitalRepository).findById(1L);
        verify(hospitalRepository).save(any(Hospital.class));
    }

    @Test
    @DisplayName("Should assign address to hospital")
    void testAssignAddressToHospital_Success() {
        HospitalAddressRequestDTO addressDTO = new HospitalAddressRequestDTO();
        addressDTO.setAddressLine1("456 Oak Ave");
        addressDTO.setCity("Another City");

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(hospitalAddressServiceImpl.createHospitalAddress(eq(1L), any(HospitalAddressRequestDTO.class)))
                .thenReturn(testAddress);
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(testHospital);

        HospitalResponseDTO result = hospitalService.assignAddressToHospital(1L, addressDTO);

        assertNotNull(result);
        verify(hospitalRepository).findById(1L);
        verify(hospitalAddressServiceImpl).createHospitalAddress(eq(1L), any(HospitalAddressRequestDTO.class));
        verify(hospitalRepository).save(any(Hospital.class));
    }

    @Test
    @DisplayName("Should throw exception when hospital not found during address assignment")
    void testAssignAddressToHospital_HospitalNotFound() {
        HospitalAddressRequestDTO addressDTO = new HospitalAddressRequestDTO();
        when(hospitalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> hospitalService.assignAddressToHospital(1L, addressDTO));

        verify(hospitalRepository).findById(1L);
    }

    @Test
    @DisplayName("Should assign department to hospital")
    void testAssignDepartment_Success() throws BusinessException {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(hospitalDepartmentRepository.existsByHospitalIdAndDepartmentId(1L, 1L))
                .thenReturn(false);
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(testHospital);

        hospitalService.assignDepartment(1L, 1L);

        verify(hospitalRepository).findById(1L);
        verify(departmentRepository).findById(1L);
        verify(hospitalDepartmentRepository).existsByHospitalIdAndDepartmentId(1L, 1L);
        verify(hospitalRepository).save(any(Hospital.class));
    }

    @Test
    @DisplayName("Should throw exception when hospital not found during department assignment")
    void testAssignDepartment_HospitalNotFound() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> hospitalService.assignDepartment(1L, 1L));

        verify(hospitalRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when department not found during assignment")
    void testAssignDepartment_DepartmentNotFound() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> hospitalService.assignDepartment(1L, 1L));

        verify(hospitalRepository).findById(1L);
        verify(departmentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when department already assigned")
    void testAssignDepartment_AlreadyAssigned() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(hospitalDepartmentRepository.existsByHospitalIdAndDepartmentId(1L, 1L))
                .thenReturn(true);

        assertThrows(BusinessException.class,
                () -> hospitalService.assignDepartment(1L, 1L));

        verify(hospitalRepository).findById(1L);
        verify(departmentRepository).findById(1L);
        verify(hospitalDepartmentRepository).existsByHospitalIdAndDepartmentId(1L, 1L);
    }

    @Test
    @DisplayName("Should remove department from hospital")
    void testRemoveDepartment_Success() throws BusinessException {
        HospitalDepartment hospitalDepartment = new HospitalDepartment();
        when(hospitalDepartmentRepository.findByHospitalIdAndDepartmentId(1L, 1L))
                .thenReturn(Optional.of(hospitalDepartment));

        hospitalService.removeDepartment(1L, 1L);

        verify(hospitalDepartmentRepository).findByHospitalIdAndDepartmentId(1L, 1L);
        verify(hospitalDepartmentRepository).delete(hospitalDepartment);
    }

    @Test
    @DisplayName("Should throw exception when department not assigned during removal")
    void testRemoveDepartment_NotAssigned() {
        when(hospitalDepartmentRepository.findByHospitalIdAndDepartmentId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> hospitalService.removeDepartment(1L, 1L));

        verify(hospitalDepartmentRepository).findByHospitalIdAndDepartmentId(1L, 1L);
    }

    @Test
    @DisplayName("Should delete hospital successfully")
    void testDeleteHospital_Success() {
        when(hospitalRepository.existsById(1L)).thenReturn(true);

        hospitalService.deleteHospital(1L);

        verify(hospitalRepository).existsById(1L);
        verify(hospitalRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when hospital not found during deletion")
    void testDeleteHospital_NotFound() {
        when(hospitalRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> hospitalService.deleteHospital(1L));

        verify(hospitalRepository).existsById(1L);
        verify(hospitalRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should check if hospital exists")
    void testExistsById_True() {
        when(hospitalRepository.existsById(1L)).thenReturn(true);

        boolean result = hospitalService.existsById(1L);

        assertTrue(result);
        verify(hospitalRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should check if hospital does not exist")
    void testExistsById_False() {
        when(hospitalRepository.existsById(1L)).thenReturn(false);

        boolean result = hospitalService.existsById(1L);

        assertFalse(result);
        verify(hospitalRepository).existsById(1L);
    }
}
