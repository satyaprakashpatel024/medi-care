package com.care.medi.services;

import com.care.medi.dtos.request.PrescriptionRequestDTO;
import com.care.medi.dtos.response.PrescriptionResponseDTO;
import com.care.medi.entity.Appointment;
import com.care.medi.entity.AppointmentStatus;
import com.care.medi.entity.Patient;
import com.care.medi.entity.Prescription;
import com.care.medi.exception.InvalidRequestException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.AppointmentRepository;
import com.care.medi.repository.PrescriptionRepository;
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
@DisplayName("PrescriptionService Unit Tests")
class PrescriptionServiceImplTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PatientService patientService;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private PrescriptionServiceImpl prescriptionService;

    private PrescriptionRequestDTO requestDTO;
    private Prescription testPrescription;
    private PrescriptionResponseDTO responseDTO;
    private Appointment testAppointment;

    @BeforeEach
    void setUp() {
        requestDTO = new PrescriptionRequestDTO();
        requestDTO.setAppointmentId(1L);
        requestDTO.setPatientId(1L);
        requestDTO.setDoctorId(1L);
        requestDTO.setMedications("Paracetamol");

        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setStatus(AppointmentStatus.SCHEDULED);
        Patient patient = new Patient();
        patient.setId(1L);
        testAppointment.setPatient(patient);

        testPrescription = new Prescription();
        testPrescription.setId(1L);
        testPrescription.setAppointment(testAppointment);
        testPrescription.setMedications("Paracetamol");

        responseDTO = PrescriptionResponseDTO.builder()
                .id(1L)
                .medications("Paracetamol")
                .build();
    }

    @Test
    @DisplayName("Should get prescription by patient id")
    void testGetPrescriptionByPatientId() {
        when(patientService.existsByIdAndHospitalId(1L, 1L)).thenReturn(true);

        // Mock the Page response manually since method returns Page<PrescriptionResponseDTO> instead of Page<Prescription>
        Page<PrescriptionResponseDTO> pageResponse = new PageImpl<>(List.of(responseDTO));
        when(prescriptionRepository.findByPatientId(eq(1L), any(Pageable.class))).thenReturn(pageResponse);

        Page<PrescriptionResponseDTO> result = prescriptionService.getPrescriptionByPatientId(1L, 1L, 0, 10, "id");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(patientService).existsByIdAndHospitalId(1L, 1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid patient id")
    void testGetPrescriptionByPatientId_NotFound() {
        when(patientService.existsByIdAndHospitalId(1L, 1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> prescriptionService.getPrescriptionByPatientId(1L, 1L, 0, 10, "id"));
        verify(patientService).existsByIdAndHospitalId(1L, 1L);
    }

    @Test
    @DisplayName("Should get prescription by appointment id")
    void testGetPrescriptionByAppointmentId() {
        when(appointmentService.existsByIdAndHospitalId(1L, 1L)).thenReturn(true);
        Page<Prescription> page = new PageImpl<>(List.of(testPrescription));
        when(prescriptionRepository.findByAppointmentId(eq(1L), any(Pageable.class))).thenReturn(page);

        Page<PrescriptionResponseDTO> result = prescriptionService.getPrescriptionByAppointmentId(1L, 1L, 0, 10, "id");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Paracetamol", result.getContent().get(0).medications());
        verify(appointmentService).existsByIdAndHospitalId(1L, 1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid appointment id")
    void testGetPrescriptionByAppointmentId_NotFound() {
        when(appointmentService.existsByIdAndHospitalId(1L, 1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> prescriptionService.getPrescriptionByAppointmentId(1L, 1L, 0, 10, "id"));
        verify(appointmentService).existsByIdAndHospitalId(1L, 1L);
    }

    @Test
    @DisplayName("Should assign prescription to appointment")
    void testAssignPrescriptionToAppointment() {
        when(appointmentService.isAppointmentContextValid(1L, 1L, 1L, 1L)).thenReturn(true);
        when(appointmentRepository.findByIdAndStatusIn(eq(1L), anyList())).thenReturn(Optional.of(testAppointment));
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(testPrescription);
        // doNothing().when(appointmentService).updateAppointmentStatus(1L, AppointmentStatus.COMPLETED);

        PrescriptionResponseDTO result = prescriptionService.assignPrescriptionToAppointment(1L, requestDTO);

        assertNotNull(result);
        assertEquals("Paracetamol", result.medications());
        verify(appointmentService).isAppointmentContextValid(1L, 1L, 1L, 1L);
        verify(appointmentRepository).findByIdAndStatusIn(eq(1L), anyList());
        verify(prescriptionRepository).save(any(Prescription.class));
        verify(appointmentService).updateAppointmentStatus(1L, AppointmentStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should throw InvalidRequestException for invalid appointment context")
    void testAssignPrescriptionToAppointment_InvalidContext() {
        when(appointmentService.isAppointmentContextValid(1L, 1L, 1L, 1L)).thenReturn(false);

        assertThrows(InvalidRequestException.class, () -> prescriptionService.assignPrescriptionToAppointment(1L, requestDTO));
        verify(appointmentService).isAppointmentContextValid(1L, 1L, 1L, 1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment not found for prescription")
    void testAssignPrescriptionToAppointment_AppointmentNotFound() {
        when(appointmentService.isAppointmentContextValid(1L, 1L, 1L, 1L)).thenReturn(true);
        when(appointmentRepository.findByIdAndStatusIn(eq(1L), anyList())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> prescriptionService.assignPrescriptionToAppointment(1L, requestDTO));
    }
}
