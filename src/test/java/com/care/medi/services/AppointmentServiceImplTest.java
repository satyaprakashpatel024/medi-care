package com.care.medi.services;

import com.care.medi.dtos.request.AppointmentRequestDTO;
import com.care.medi.dtos.request.PatientRequestDTO;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import com.care.medi.dtos.response.AppointmentSummaryResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.*;
import com.care.medi.services.kafka.EmailNotificationProducer;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService Unit Tests")
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private PatientServiceImpl patientService;
    @Mock
    private HospitalRepository hospitalRepository;
    @Mock
    private PrescriptionRepository prescriptionRepository;
    @Mock
    private EmailNotificationProducer emailNotificationProducer;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Appointment testAppointment;
    private Doctor testDoctor;
    private Patient testPatient;
    private Department testDepartment;
    private Hospital testHospital;
    private AppointmentRequestDTO appointmentRequestDTO;
    private Users testDoctorUser;
    private Users testPatientUser;

    @BeforeEach
    void setUp() {
        testHospital = new Hospital();
        testHospital.setId(1L);
        testHospital.setName("Test Hospital");

        testDepartment = new Department();
        testDepartment.setId(1L);
        testDepartment.setName("Cardiology");

        testDoctorUser = new Users();
        testDoctorUser.setId(1L);
        testDoctorUser.setEmail("doctor@test.com");
        testDoctorUser.setRole(Role.DOCTOR);

        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setUserId(1L);
        testDoctor.setHospital(testHospital);
        testDoctor.setDepartment(testDepartment);
        testDoctor.setFirstName("John");
        testDoctor.setLastName("Doe");
        testDoctor.setActive(true);

        testPatientUser = new Users();
        testPatientUser.setId(2L);
        testPatientUser.setEmail("patient@test.com");
        testPatientUser.setRole(Role.PATIENT);

        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setUser(testPatientUser);
        testPatient.setHospital(testHospital);
        testPatient.setFirstName("Jane");
        testPatient.setLastName("Smith");
        testPatient.setPhone("9876543210");

        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setHospitalId(1L);
        testAppointment.setDoctor(testDoctor);
        testAppointment.setPatient(testPatient);
        testAppointment.setDepartment(testDepartment);
        testAppointment.setAppointmentDate(LocalDate.now().plusDays(1));
        testAppointment.setStartTime(LocalTime.of(10, 0));
        testAppointment.setStatus(AppointmentStatus.SCHEDULED);

        appointmentRequestDTO = new AppointmentRequestDTO();
        appointmentRequestDTO.setDoctorId(1L);
        appointmentRequestDTO.setDepartmentId(1L);
        appointmentRequestDTO.setAppointmentDate("2024-12-25");
        appointmentRequestDTO.setAppointmentTime("10:00");

        PatientRequestDTO patientReq = new PatientRequestDTO();
        patientReq.setEmail("newpatient@test.com");
        patientReq.setFirstName("Test");
        patientReq.setLastName("Patient");
        patientReq.setPhone("9876543210");
        appointmentRequestDTO.setPatient(patientReq);
    }

    @Test
    @DisplayName("Should find appointment by ID with specific statuses")
    void testFindByIdAndStatusIn_Found() {
        when(appointmentRepository.findByIdAndStatusIn(1L, List.of(AppointmentStatus.SCHEDULED)))
                .thenReturn(Optional.of(testAppointment));

        Optional<Appointment> result = appointmentService.findByIdAndStatusIn(1L,
                List.of(AppointmentStatus.SCHEDULED));

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(appointmentRepository).findByIdAndStatusIn(1L, List.of(AppointmentStatus.SCHEDULED));
    }

    @Test
    @DisplayName("Should not find appointment by ID with specific statuses")
    void testFindByIdAndStatusIn_NotFound() {
        when(appointmentRepository.findByIdAndStatusIn(1L, List.of(AppointmentStatus.COMPLETED)))
                .thenReturn(Optional.empty());

        Optional<Appointment> result = appointmentService.findByIdAndStatusIn(1L,
                List.of(AppointmentStatus.COMPLETED));

        assertFalse(result.isPresent());
        verify(appointmentRepository).findByIdAndStatusIn(1L, List.of(AppointmentStatus.COMPLETED));
    }

    @Test
    @DisplayName("Should validate appointment context")
    void testIsAppointmentContextValid_True() {
        when(appointmentRepository.isAppointmentContextValid(1L, 1L, 1L, 1L))
                .thenReturn(true);

        boolean result = appointmentService.isAppointmentContextValid(1L, 1L, 1L, 1L);

        assertTrue(result);
        verify(appointmentRepository).isAppointmentContextValid(1L, 1L, 1L, 1L);
    }

    @Test
    @DisplayName("Should validate appointment existence by ID and hospital")
    void testExistsByIdAndHospitalId_True() {
        when(appointmentRepository.existsByIdAndHospitalId(1L, 1L))
                .thenReturn(true);

        boolean result = appointmentService.existsByIdAndHospitalId(1L, 1L);

        assertTrue(result);
        verify(appointmentRepository).existsByIdAndHospitalId(1L, 1L);
    }

    @Test
    @DisplayName("Should validate appointment existence by ID, doctor and hospital")
    void testExistsByIdAndDoctorIdAndHospitalId_True() {
        when(appointmentRepository.existsByIdAndDoctorIdAndHospitalId(1L, 1L, 1L))
                .thenReturn(true);

        boolean result = appointmentService.existsByIdAndDoctorIdAndHospitalId(1L, 1L, 1L);

        assertTrue(result);
        verify(appointmentRepository).existsByIdAndDoctorIdAndHospitalId(1L, 1L, 1L);
    }

    @Test
    @DisplayName("Should get all appointments by hospital and date")
    void testGetAllAppointmentsByHospitalAndDate_Success() {
        LocalDate date = LocalDate.now();
        Page<AppointmentSummaryResponseDTO> appointmentPage = new PageImpl<>(List.of());
        when(appointmentRepository.findByHospitalIdAndAppointmentDateBetween(
                eq(1L), any(LocalDate.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(appointmentPage);

        Page<AppointmentSummaryResponseDTO> result = appointmentService.getAllAppointmentsByHospitalAndDate(
                1L, 0, 10, "id", date);

        assertNotNull(result);
        verify(appointmentRepository).findByHospitalIdAndAppointmentDateBetween(
                eq(1L), any(LocalDate.class), any(LocalDate.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get appointment by ID and hospital")
    void testGetAppointmentByIdAndHospital_Success() {
        when(appointmentRepository.findByIdAndHospitalId(1L, 1L))
                .thenReturn(Optional.of(testAppointment));

        AppointmentResponseDTO result = appointmentService.getAppointmentByIdAndHospital(1L, 1L);

        assertNotNull(result);
        verify(appointmentRepository).findByIdAndHospitalId(1L, 1L);
    }

    @Test
    @DisplayName("Should throw exception when appointment not found by ID and hospital")
    void testGetAppointmentByIdAndHospital_NotFound() {
        when(appointmentRepository.findByIdAndHospitalId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.getAppointmentByIdAndHospital(1L, 1L));

        verify(appointmentRepository).findByIdAndHospitalId(1L, 1L);
    }

    @Test
    @DisplayName("Should update appointment status successfully")
    void testUpdateAppointmentStatus_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        AppointmentResponseDTO result = appointmentService.updateAppointmentStatus(1L, AppointmentStatus.COMPLETED);

        assertNotNull(result);
        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should cancel appointment successfully")
    void testCancelAppointment_Success() {
        when(appointmentRepository.findByIdAndHospitalId(1L, 1L))
                .thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.saveAndFlush(any(Appointment.class)))
                .thenReturn(testAppointment);

        appointmentService.cancelAppointment(1L, 1L);

        verify(appointmentRepository).findByIdAndHospitalId(1L, 1L);
        verify(appointmentRepository).saveAndFlush(any(Appointment.class));
    }

    @Test
    @DisplayName("Should throw exception when appointment not found during cancellation")
    void testCancelAppointment_NotFound() {
        when(appointmentRepository.findByIdAndHospitalId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.cancelAppointment(1L, 1L));

        verify(appointmentRepository).findByIdAndHospitalId(1L, 1L);
    }

    @Test
    @DisplayName("Should delete appointment successfully")
    void testDeleteAppointment_Success() {
        when(appointmentRepository.findByIdAndHospitalId(1L, 1L))
                .thenReturn(Optional.of(testAppointment));

        appointmentService.deleteAppointment(1L, 1L);

        verify(appointmentRepository).findByIdAndHospitalId(1L, 1L);
        verify(appointmentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when appointment not found during deletion")
    void testDeleteAppointment_NotFound() {
        when(appointmentRepository.findByIdAndHospitalId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.deleteAppointment(1L, 1L));

        verify(appointmentRepository).findByIdAndHospitalId(1L, 1L);
    }

    @Test
    @DisplayName("Should get appointments by hospital and patient")
    void testGetAppointmentsByHospitalAndPatient_Success() {
        Page<AppointmentResponseDTO> appointmentPage = new PageImpl<>(List.of());
        when(appointmentRepository.findByHospitalIdAndPatientId(eq(1L), eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testAppointment)));

        Page<AppointmentResponseDTO> result = appointmentService.getAppointmentsByHospitalAndPatient(
                1L, 1L, 0, 10, "id");

        assertNotNull(result);
        verify(appointmentRepository).findByHospitalIdAndPatientId(eq(1L), eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get appointments by hospital, status and date")
    void testGetAppointmentsByHospitalAndStatusAndDate_Success() {
        LocalDate date = LocalDate.now();
        Page<AppointmentListResponseDTO> appointmentPage = new PageImpl<>(List.of());
        when(appointmentRepository.findByHospitalIdAndStatusAndAppointmentDateBetween(
                eq(1L), eq(AppointmentStatus.SCHEDULED), any(LocalDate.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(appointmentPage);

        Page<AppointmentListResponseDTO> result = appointmentService.getAppointmentsByHospitalAndStatusAndDate(
                1L, AppointmentStatus.SCHEDULED, 0, 10, "id", date);

        assertNotNull(result);
        verify(appointmentRepository).findByHospitalIdAndStatusAndAppointmentDateBetween(
                eq(1L), eq(AppointmentStatus.SCHEDULED), any(LocalDate.class), any(LocalDate.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get appointments by doctor, hospital and date")
    void testGetAppointmentsByDoctorAndHospitalIdAndDate_Success() {
        LocalDate date = LocalDate.now();
        Page<Appointment> appointmentPage = new PageImpl<>(List.of(testAppointment));
        when(appointmentRepository.findByDoctorIdAndHospitalIdAndAppointmentDateBetween(
                eq(1L), eq(1L), any(LocalDate.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(appointmentPage);

        Page<AppointmentListResponseDTO> result = appointmentService.getAppointmentsByDoctorAndHospitalIdAndDate(
                1L, 1L, 0, 10, "id", date);

        assertNotNull(result);
        verify(appointmentRepository).findByDoctorIdAndHospitalIdAndAppointmentDateBetween(
                eq(1L), eq(1L), any(LocalDate.class), any(LocalDate.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get appointments by patient and date")
    void testGetAppointmentsByPatientAndDate_Success() {
        LocalDate date = LocalDate.now();
        Page<AppointmentResponseDTO> appointmentPage = new PageImpl<>(List.of());
        when(appointmentRepository.findByPatientIdAndAppointmentDateBetween(
                eq(1L), any(LocalDate.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testAppointment)));

        Page<AppointmentResponseDTO> result = appointmentService.getAppointmentsByPatientAndDate(
                1L, date, 0, 10, "id");

        assertNotNull(result);
        verify(appointmentRepository).findByPatientIdAndAppointmentDateBetween(
                eq(1L), any(LocalDate.class), any(LocalDate.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should check for conflicting appointments")
    void testCheckForConflictingAppointment_True() {
        LocalDate date = LocalDate.now();
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(10, 10);

        when(appointmentRepository.existsConflictingAppointment(1L, 1L, date, startTime, endTime))
                .thenReturn(true);

        boolean result = appointmentService.checkForConflictingAppointment(1L, 1L, date, startTime, endTime);

        assertTrue(result);
        verify(appointmentRepository).existsConflictingAppointment(1L, 1L, date, startTime, endTime);
    }

    @Test
    @DisplayName("Should return false when no conflicting appointments")
    void testCheckForConflictingAppointment_False() {
        LocalDate date = LocalDate.now();
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(10, 10);

        when(appointmentRepository.existsConflictingAppointment(1L, 1L, date, startTime, endTime))
                .thenReturn(false);

        boolean result = appointmentService.checkForConflictingAppointment(1L, 1L, date, startTime, endTime);

        assertFalse(result);
        verify(appointmentRepository).existsConflictingAppointment(1L, 1L, date, startTime, endTime);
    }
}
