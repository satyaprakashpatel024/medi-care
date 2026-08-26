package com.care.medi.services;

import com.care.medi.dtos.request.DoctorRequestDTO;
import com.care.medi.dtos.request.DoctorUpdateRequestDTO;
import com.care.medi.dtos.response.DoctorListResponseDTO;
import com.care.medi.dtos.response.DoctorResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.DuplicateResourceException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.DepartmentRepository;
import com.care.medi.repository.DoctorRepository;
import com.care.medi.repository.HospitalRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorService Unit Tests")
class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private HospitalRepository hospitalRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private AddressServiceImpl addressService;
    @Mock
    private AppointmentServiceImpl appointmentService;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Doctor testDoctor;
    private Users testUser;
    private Hospital testHospital;
    private Department testDepartment;
    private DoctorRequestDTO doctorRequestDTO;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(1L);
        testUser.setEmail("doctor@test.com");
        testUser.setRole(Role.DOCTOR);

        testHospital = new Hospital();
        testHospital.setId(1L);
        testHospital.setName("Test Hospital");
        testHospital.setPhone("1234567890");

        testDepartment = new Department();
        testDepartment.setId(1L);
        testDepartment.setName("Cardiology");

        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setUserId(1L);
        testDoctor.setHospital(testHospital);
        testDoctor.setDepartment(testDepartment);
        testDoctor.setFirstName("John");
        testDoctor.setLastName("Doe");
        testDoctor.setGender(Gender.MALE);
        testDoctor.setPhone("9876543210");
        testDoctor.setSpeciality("Cardiology");
        testDoctor.setBloodGroup(BloodGroup.O_POS);
        testDoctor.setActive(true);
        testDoctor.setDateOfBirth(LocalDate.of(1980, 5, 15));

        doctorRequestDTO = new DoctorRequestDTO();
        doctorRequestDTO.setEmail("newdoctor@test.com");
        doctorRequestDTO.setFirstName("Jane");
        doctorRequestDTO.setLastName("Smith");
        doctorRequestDTO.setPhone("9876543211");
        doctorRequestDTO.setSpeciality("Neurology");
        doctorRequestDTO.setGender("FEMALE");
        doctorRequestDTO.setBloodType("AB_POS");
        doctorRequestDTO.setDateOfBirth(LocalDate.of(1985, 3, 20));
        doctorRequestDTO.setEmergencyContact("9876543212");
        doctorRequestDTO.setDepartmentId(1L);
        doctorRequestDTO.setHospitalId(1L);
    }

    @Test
    @DisplayName("Should create doctor successfully in hospital")
    void testCreateDoctorInHospital_Success() {
        when(usersRepository.existsByEmail("newdoctor@test.com")).thenReturn(false);
        when(hospitalRepository.existsById(1L)).thenReturn(true);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(usersRepository.save(any(Users.class))).thenReturn(testUser);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        DoctorResponseDTO result = doctorService.createDoctorInHospital(1L, doctorRequestDTO);

        assertNotNull(result);
        verify(usersRepository).existsByEmail("newdoctor@test.com");
        verify(hospitalRepository).existsById(1L);
        verify(departmentRepository).findById(1L);
        verify(usersRepository).save(any(Users.class));
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testCreateDoctorInHospital_DuplicateEmail() {
        when(usersRepository.existsByEmail("newdoctor@test.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> doctorService.createDoctorInHospital(1L, doctorRequestDTO));

        verify(usersRepository).existsByEmail("newdoctor@test.com");
        verify(hospitalRepository, never()).existsById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when hospital not found")
    void testCreateDoctorInHospital_HospitalNotFound() {
        when(usersRepository.existsByEmail("newdoctor@test.com")).thenReturn(false);
        when(hospitalRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> doctorService.createDoctorInHospital(1L, doctorRequestDTO));

        verify(usersRepository).existsByEmail("newdoctor@test.com");
        verify(hospitalRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should throw exception when department not found")
    void testCreateDoctorInHospital_DepartmentNotFound() {
        when(usersRepository.existsByEmail("newdoctor@test.com")).thenReturn(false);
        when(hospitalRepository.existsById(1L)).thenReturn(true);
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> doctorService.createDoctorInHospital(1L, doctorRequestDTO));

        verify(usersRepository).existsByEmail("newdoctor@test.com");
        verify(hospitalRepository).existsById(1L);
        verify(departmentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get active doctors by hospital with pagination")
    void testGetAllActiveDoctorsByHospital_Success() {
        Page<Doctor> doctorPage = new PageImpl<>(List.of(testDoctor));
        when(doctorRepository.findByHospitalIdAndIsActiveTrue(eq(1L), any(Pageable.class)))
                .thenReturn(doctorPage);

        Page<DoctorListResponseDTO> result = doctorService.getAllActiveDoctorsByHospital(1L, 0, 10, "id");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(doctorRepository).findByHospitalIdAndIsActiveTrue(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get active doctors by department and hospital")
    void testGetActiveDoctorsByDepartmentAndHospital_Success() {
        Page<Doctor> doctorPage = new PageImpl<>(List.of(testDoctor));
        when(doctorRepository.findByHospitalIdAndDepartmentIdAndIsActiveTrue(eq(1L), eq(1L), any(Pageable.class)))
                .thenReturn(doctorPage);

        Page<DoctorListResponseDTO> result = doctorService.getActiveDoctorsByDepartmentAndHospital(1L, 1L, 0, 10, "id");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(doctorRepository).findByHospitalIdAndDepartmentIdAndIsActiveTrue(eq(1L), eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get active doctors by speciality and hospital")
    void testGetActiveDoctorsBySpecialityAndHospital_Success() {
        Page<Doctor> doctorPage = new PageImpl<>(List.of(testDoctor));
        when(doctorRepository.findByHospitalIdAndSpecialityContainingIgnoreCaseAndIsActiveTrue(
                eq(1L), eq("Cardiology"), any(Pageable.class)))
                .thenReturn(doctorPage);

        Page<DoctorListResponseDTO> result = doctorService.getActiveDoctorsBySpecialityAndHospital(
                "Cardiology", 1L, 0, 10, "id");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(doctorRepository).findByHospitalIdAndSpecialityContainingIgnoreCaseAndIsActiveTrue(
                eq(1L), eq("Cardiology"), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get doctor by ID and hospital with addresses")
    void testGetDoctorByIdAndHospital_Success() {
        when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L))
                .thenReturn(Optional.of(testDoctor));
        when(addressService.getAddressesByDoctorId(1L))
                .thenReturn(List.of());

        DoctorResponseDTO result = doctorService.getDoctorByIdAndHospital(1L, 1L);

        assertNotNull(result);
        verify(doctorRepository).findByIdAndHospitalIdAndIsActiveTrue(1L, 1L);
        verify(addressService).getAddressesByDoctorId(1L);
    }

    @Test
    @DisplayName("Should throw exception when doctor not found by ID and hospital")
    void testGetDoctorByIdAndHospital_NotFound() {
        when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> doctorService.getDoctorByIdAndHospital(1L, 1L));

        verify(doctorRepository).findByIdAndHospitalIdAndIsActiveTrue(1L, 1L);
    }

    @Test
    @DisplayName("Should get all active doctors with pagination")
    void testGetAllActiveDoctors_Success() {
        Page<Doctor> doctorPage = new PageImpl<>(List.of(testDoctor));
        when(doctorRepository.findByIsActiveTrue(any(Pageable.class)))
                .thenReturn(doctorPage);

        Page<DoctorListResponseDTO> result = doctorService.getAllActiveDoctors(0, 10, "id");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(doctorRepository).findByIsActiveTrue(any(Pageable.class));
    }

    @Test
    @DisplayName("Should update doctor successfully")
    void testUpdateDoctorByIdAndHospital_Success() {
        DoctorUpdateRequestDTO updateDTO = new DoctorUpdateRequestDTO();
        updateDTO.setFirstName("UpdatedJohn");
        updateDTO.setPhone("9999999999");
        updateDTO.setSpeciality("UpdatedSpeciality");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        DoctorResponseDTO result = doctorService.updateDoctorByIdAndHospital(1L, 1L, updateDTO);

        assertNotNull(result);
        verify(doctorRepository).findById(1L);
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Should throw exception when doctor not found during update")
    void testUpdateDoctorByIdAndHospital_NotFound() {
        DoctorUpdateRequestDTO updateDTO = new DoctorUpdateRequestDTO();
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> doctorService.updateDoctorByIdAndHospital(1L, 1L, updateDTO));

        verify(doctorRepository).findById(1L);
    }

    @Test
    @DisplayName("Should delete doctor successfully (soft delete)")
    void testDeleteDoctorByIdAndHospital_Success() {
        when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L))
                .thenReturn(Optional.of(testDoctor));

        doctorService.deleteDoctorByIdAndHospital(1L, 1L);

        assertFalse(testDoctor.isActive());
        verify(doctorRepository).findByIdAndHospitalIdAndIsActiveTrue(1L, 1L);
    }

    @Test
    @DisplayName("Should throw exception when doctor not found during delete")
    void testDeleteDoctorByIdAndHospital_NotFound() {
        when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> doctorService.deleteDoctorByIdAndHospital(1L, 1L));

        verify(doctorRepository).findByIdAndHospitalIdAndIsActiveTrue(1L, 1L);
    }

    @Test
    @DisplayName("Should get appointments by doctor, hospital and date")
    void testGetAppointmentsByDoctorAndHospitalAndDate_Success() {
        LocalDate date = LocalDate.now();
        when(doctorRepository.existsByIdAndHospitalId(1L, 1L)).thenReturn(true);
        when(appointmentService.getAppointmentsByDoctorAndHospitalIdAndDate(
                eq(1L), eq(1L), eq(0), eq(10), eq("id"), eq(date)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<?> result = doctorService.getAppointmentsByDoctorAndHospitalAndDate(
                1L, 1L, date, 0, 10, "id");

        assertNotNull(result);
        verify(doctorRepository).existsByIdAndHospitalId(1L, 1L);
        verify(appointmentService).getAppointmentsByDoctorAndHospitalIdAndDate(
                eq(1L), eq(1L), eq(0), eq(10), eq("id"), eq(date));
    }

    @Test
    @DisplayName("Should throw exception when doctor not found for appointments")
    void testGetAppointmentsByDoctorAndHospitalAndDate_DoctorNotFound() {
        LocalDate date = LocalDate.now();
        when(doctorRepository.existsByIdAndHospitalId(1L, 1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> doctorService.getAppointmentsByDoctorAndHospitalAndDate(
                        1L, 1L, date, 0, 10, "id"));

        verify(doctorRepository).existsByIdAndHospitalId(1L, 1L);
    }
}
