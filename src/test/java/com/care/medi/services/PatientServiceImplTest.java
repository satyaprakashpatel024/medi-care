package com.care.medi.services;

import com.care.medi.dtos.request.PatientRequestDTO;
import com.care.medi.dtos.request.PatientUpdateRequestDTO;
import com.care.medi.dtos.response.PatientListResponseDTO;
import com.care.medi.dtos.response.PatientResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.DuplicateResourceException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.PatientRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientService Unit Tests")
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private HospitalService hospitalService;
    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient testPatient;
    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(1L);
        testUser.setEmail("patient@test.com");
        testUser.setRole(Role.PATIENT);

        Hospital testHospital = new Hospital();
        testHospital.setId(1L);
        testHospital.setName("Test Hospital");

        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setHospital(testHospital);
        testPatient.setUser(testUser);
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");
        testPatient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testPatient.setGender(Gender.MALE);
        testPatient.setPhone("1234567890");
        testPatient.setBloodGroup(BloodGroup.O_POS);
    }

    @Test
    @DisplayName("Should retrieve patient by ID and hospital ID")
    void testGetPatientByIdAndHospitalId_Success() {
        when(patientRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testPatient));
        PatientResponseDTO result = patientService.getPatientByIdAndHospitalId(1L, 1L);
        assertNotNull(result);
        assertEquals("John", result.firstName());
        verify(patientRepository).findByIdAndHospitalId(1L, 1L);
    }

    @Test
    @DisplayName("Should throw exception when patient not found")
    void testGetPatientByIdAndHospitalId_NotFound() {
        when(patientRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> patientService.getPatientByIdAndHospitalId(1L, 1L));
    }

    @Test
    @DisplayName("Should create patient successfully")
    void testCreatePatientInHospital_Success() {

        PatientRequestDTO requestDTO = new PatientRequestDTO();
        requestDTO.setEmail("newpatient@test.com");
        requestDTO.setFirstName("Jane");
        requestDTO.setLastName("Smith");
        requestDTO.setPhone("9876543211");
        requestDTO.setEmergencyContact("9876543210");
        requestDTO.setGender("FEMALE");
        requestDTO.setBloodGroup("O_POS");   // Use a valid enum value
        requestDTO.setDateOfBirth(LocalDate.of(1995, 5, 10));

        when(usersRepository.existsByEmail("newpatient@test.com"))
                .thenReturn(false);

        when(usersRepository.save(any(Users.class)))
                .thenReturn(testUser);

        when(patientRepository.save(any(Patient.class)))
                .thenReturn(testPatient);

        PatientResponseDTO result = patientService.createPatientInHospital(1L, requestDTO);

        assertNotNull(result);

        verify(usersRepository).existsByEmail("newpatient@test.com");
        verify(usersRepository).save(any(Users.class));
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testCreatePatientInHospital_DuplicateEmail() {
        PatientRequestDTO requestDTO = new PatientRequestDTO();
        requestDTO.setEmail("duplicate@test.com");
        when(usersRepository.existsByEmail("duplicate@test.com")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> patientService.createPatientInHospital(1L, requestDTO));
    }

    @Test
    @DisplayName("Should update patient successfully")
    void testUpdatePatientInHospital_Success() {
        PatientUpdateRequestDTO updateDTO = new PatientUpdateRequestDTO();
        updateDTO.setFirstName("UpdatedJohn");
        updateDTO.setPhone("9876543210");

        when(patientRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testPatient));
        when(patientRepository.saveAndFlush(any())).thenReturn(testPatient);

        PatientResponseDTO result = patientService.updatePatientInHospital(1L, 1L, updateDTO);
        assertNotNull(result);
        verify(patientRepository).findByIdAndHospitalId(1L, 1L);
        verify(patientRepository).saveAndFlush(any());
    }

    @Test
    @DisplayName("Should throw exception when patient not found during update")
    void testUpdatePatientInHospital_NotFound() {
        PatientUpdateRequestDTO updateDTO = new PatientUpdateRequestDTO();
        when(patientRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> patientService.updatePatientInHospital(1L, 1L, updateDTO));
    }

    @Test
    @DisplayName("Should delete patient successfully")
    void testDeletePatientFromHospital_Success() {
        patientService.deletePatientFromHospital(1L, 1L);
        verify(patientRepository).deleteByIdAndHospitalId(1L, 1L);
    }

    @Test
    @DisplayName("Should get all patients by hospital with pagination")
    void testGetAllPatientsByHospital_Success() {
        Page<PatientListResponseDTO> expectedPage = new PageImpl<>(List.of());
        when(hospitalService.existsById(1L)).thenReturn(true);
        when(patientRepository.findAllByHospitalId(eq(1L), any())).thenReturn(expectedPage);

        Page<PatientListResponseDTO> result = patientService.getAllPatientsByHospital(1L, 0, 10, "id");
        assertNotNull(result);
        verify(hospitalService).existsById(1L);
    }

    @Test
    @DisplayName("Should throw exception when hospital not found")
    void testGetAllPatientsByHospital_HospitalNotFound() {
        when(hospitalService.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> patientService.getAllPatientsByHospital(1L, 0, 10, "id"));
    }

    @Test
    @DisplayName("Should check patient exists by ID and hospital ID")
    void testExistsByIdAndHospitalId() {
        when(patientRepository.existsByIdAndHospitalId(1L, 1L)).thenReturn(true);
        assertTrue(patientService.existsByIdAndHospitalId(1L, 1L));
        verify(patientRepository).existsByIdAndHospitalId(1L, 1L);
    }
}
