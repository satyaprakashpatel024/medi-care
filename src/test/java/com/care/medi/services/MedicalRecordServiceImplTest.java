package com.care.medi.services;

import com.care.medi.dtos.request.MedicalRecordRequestDTO;
import com.care.medi.dtos.request.MedicalRecordUpdateRequestDTO;
import com.care.medi.dtos.response.MedicalRecordListResponseDTO;
import com.care.medi.dtos.response.MedicalRecordResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.InvalidRequestException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.AppointmentRepository;
import com.care.medi.repository.DoctorRepository;
import com.care.medi.repository.MedicalRecordRepository;
import com.care.medi.repository.PatientRepository;
import com.care.medi.utils.MedicalRecordMapper;
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
@DisplayName("MedicalRecordService Unit Tests")
class MedicalRecordServiceImplTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private MedicalRecordMapper medicalRecordMapper;

    @InjectMocks
    private MedicalRecordServiceImpl medicalRecordService;

    private MedicalRecord testRecord;
    private Patient testPatient;
    private Doctor testDoctor;
    private MedicalRecordRequestDTO requestDTO;
    private MedicalRecordResponseDTO responseDTO;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        testPatient = new Patient();
        testPatient.setId(1L);

        testDoctor = new Doctor();
        testDoctor.setId(1L);

        testRecord = MedicalRecord.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .hospitalId(1L)
                .diagnosis("Fever")
                .status(RecordStatus.ACTIVE)
                .build();
        testRecord.setId(1L);

        requestDTO = new MedicalRecordRequestDTO();
        requestDTO.setPatientId(1L);
        requestDTO.setDoctorId(1L);
        requestDTO.setDiagnosis("Fever");

        responseDTO = MedicalRecordResponseDTO.builder()
                .id(1L)
                .diagnosis("Fever")
                .build();
    }

    @Test
    @DisplayName("Should create medical record successfully")
    void testCreateRecord() {
        when(patientRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.of(testDoctor));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);
        when(medicalRecordMapper.toResponseDTO(any(MedicalRecord.class))).thenReturn(responseDTO);

        MedicalRecordResponseDTO response = medicalRecordService.createRecord(1L, requestDTO);

        assertNotNull(response);
        assertEquals("Fever", response.getDiagnosis());
        verify(patientRepository).findByIdAndHospitalId(1L, 1L);
        verify(doctorRepository).findByIdAndHospitalIdAndIsActiveTrue(1L, 1L);
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }

    @Test
    @DisplayName("Should throw InvalidRequestException if appointment already has a record")
    void testCreateRecord_DuplicateAppointment() {
        requestDTO.setAppointmentId(1L);
        Appointment testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setPatient(testPatient);

        when(patientRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testAppointment));
        when(medicalRecordRepository.existsByAppointmentIdAndHospitalId(1L, 1L)).thenReturn(true);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> medicalRecordService.createRecord(1L, requestDTO));
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should get record by ID")
    void testGetRecordById() {
        when(medicalRecordRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testRecord));
        when(medicalRecordMapper.toResponseDTO(testRecord)).thenReturn(responseDTO);

        MedicalRecordResponseDTO response = medicalRecordService.getRecordById(1L, 1L);

        assertNotNull(response);
        assertEquals("Fever", response.getDiagnosis());
    }

    @Test
    @DisplayName("Should update record")
    void testUpdateRecord() {
        when(medicalRecordRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testRecord));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);
        when(medicalRecordMapper.toResponseDTO(any(MedicalRecord.class))).thenReturn(responseDTO);

        MedicalRecordUpdateRequestDTO updateDTO = new MedicalRecordUpdateRequestDTO();
        updateDTO.setDiagnosis("Severe Fever");
        updateDTO.setStatus("ARCHIVED");

        MedicalRecordResponseDTO response = medicalRecordService.updateRecord(1L, 1L, updateDTO);

        assertNotNull(response);
        assertEquals(RecordStatus.ARCHIVED, testRecord.getStatus());
        assertEquals("Severe Fever", testRecord.getDiagnosis());
        verify(medicalRecordRepository).save(testRecord);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for get record by ID if not found")
    void testGetRecordById_NotFound() {
        when(medicalRecordRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> medicalRecordService.getRecordById(1L, 1L));
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should delete record without appointment")
    void testDeleteRecord() {
        when(medicalRecordRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testRecord));

        String result = medicalRecordService.deleteRecord(1L, 1L);

        assertEquals("Medical record ID 1 has been deleted successfully.", result);
        verify(medicalRecordRepository).delete(testRecord);
    }

    @Test
    @DisplayName("Should throw InvalidRequestException when deleting record with appointment")
    void testDeleteRecord_WithAppointment() {
        Appointment appt = new Appointment();
        appt.setId(2L);
        testRecord.setAppointment(appt);
        when(medicalRecordRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testRecord));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> medicalRecordService.deleteRecord(1L, 1L));
        assertNotNull(exception.getMessage());
        verify(medicalRecordRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should get records by patient")
    void testGetRecordsByPatient() {
        when(patientRepository.existsByIdAndHospitalId(1L, 1L)).thenReturn(true);
        Page<MedicalRecord> page = new PageImpl<>(List.of(testRecord));
        when(medicalRecordRepository.findAllByPatientIdAndHospitalId(eq(1L), eq(1L), any(Pageable.class))).thenReturn(page);

        MedicalRecordListResponseDTO listDTO = new MedicalRecordListResponseDTO();
        when(medicalRecordMapper.toListResponseDTO(any(MedicalRecord.class))).thenReturn(listDTO);

        Page<MedicalRecordListResponseDTO> result = medicalRecordService.getRecordsByPatient(1L, 1L, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(medicalRecordRepository).findAllByPatientIdAndHospitalId(eq(1L), eq(1L), any(Pageable.class));
    }

    // ── getRecordByAppointmentId ─────────────────────────────────────────────

    @Test
    @DisplayName("Should get record by appointment ID successfully")
    void testGetRecordByAppointmentId_Success() {
        Appointment appt = new Appointment();
        appt.setId(1L);
        when(appointmentRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(appt));
        when(medicalRecordRepository.findByAppointmentId(1L)).thenReturn(Optional.of(testRecord));
        when(medicalRecordMapper.toResponseDTO(testRecord)).thenReturn(responseDTO);

        MedicalRecordResponseDTO result = medicalRecordService.getRecordByAppointmentId(1L, 1L);

        assertNotNull(result);
        assertEquals("Fever", result.getDiagnosis());
        verify(appointmentRepository).findByIdAndHospitalId(1L, 1L);
        verify(medicalRecordRepository).findByAppointmentId(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment not found for getRecordByAppointmentId")
    void testGetRecordByAppointmentId_AppointmentNotFound() {
        when(appointmentRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> medicalRecordService.getRecordByAppointmentId(1L, 1L));
        assertNotNull(exception.getMessage());
        verify(appointmentRepository).findByIdAndHospitalId(1L, 1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when no record for appointment")
    void testGetRecordByAppointmentId_NoRecordFound() {
        Appointment appt = new Appointment();
        appt.setId(1L);
        when(appointmentRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(appt));
        when(medicalRecordRepository.findByAppointmentId(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> medicalRecordService.getRecordByAppointmentId(1L, 1L));
        assertNotNull(exception.getMessage());
    }

    // ── getActiveRecordsByPatient ─────────────────────────────────────────────

    @Test
    @DisplayName("Should get active records by patient")
    void testGetActiveRecordsByPatient_Success() {
        when(patientRepository.existsByIdAndHospitalId(1L, 1L)).thenReturn(true);
        Page<MedicalRecord> page = new PageImpl<>(List.of(testRecord));
        when(medicalRecordRepository.findActiveByPatientIdAndHospitalId(eq(1L), eq(1L), any(Pageable.class))).thenReturn(page);

        MedicalRecordListResponseDTO listDTO = new MedicalRecordListResponseDTO();
        when(medicalRecordMapper.toListResponseDTO(any(MedicalRecord.class))).thenReturn(listDTO);

        Page<MedicalRecordListResponseDTO> result = medicalRecordService.getActiveRecordsByPatient(1L, 1L, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for getActiveRecordsByPatient when patient not found")
    void testGetActiveRecordsByPatient_PatientNotFound() {
        when(patientRepository.existsByIdAndHospitalId(1L, 1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> medicalRecordService.getActiveRecordsByPatient(1L, 1L, Pageable.unpaged()));
        assertNotNull(exception.getMessage());
    }

    // ── getLatestRecordByPatient ──────────────────────────────────────────────

    @Test
    @DisplayName("Should get latest record by patient")
    void testGetLatestRecordByPatient_Success() {
        when(patientRepository.existsByIdAndHospitalId(1L, 1L)).thenReturn(true);
        Page<MedicalRecord> page = new PageImpl<>(List.of(testRecord));
        when(medicalRecordRepository.findLatestActiveByPatientIdAndHospitalId(eq(1L), eq(1L), any(Pageable.class))).thenReturn(page);
        when(medicalRecordMapper.toResponseDTO(testRecord)).thenReturn(responseDTO);

        MedicalRecordResponseDTO result = medicalRecordService.getLatestRecordByPatient(1L, 1L);

        assertNotNull(result);
        assertEquals("Fever", result.getDiagnosis());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when no active records for patient")
    void testGetLatestRecordByPatient_NoRecords() {
        when(patientRepository.existsByIdAndHospitalId(1L, 1L)).thenReturn(true);
        Page<MedicalRecord> emptyPage = new PageImpl<>(List.of());
        when(medicalRecordRepository.findLatestActiveByPatientIdAndHospitalId(eq(1L), eq(1L), any(Pageable.class))).thenReturn(emptyPage);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> medicalRecordService.getLatestRecordByPatient(1L, 1L));
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for getLatestRecordByPatient when patient not found")
    void testGetLatestRecordByPatient_PatientNotFound() {
        when(patientRepository.existsByIdAndHospitalId(1L, 1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> medicalRecordService.getLatestRecordByPatient(1L, 1L));
        assertNotNull(exception.getMessage());
    }

    // ── getRecordsByDoctor ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should get records by doctor")
    void testGetRecordsByDoctor_Success() {
        when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.of(testDoctor));
        Page<MedicalRecord> page = new PageImpl<>(List.of(testRecord));
        when(medicalRecordRepository.findAllByDoctorIdAndHospitalId(eq(1L), eq(1L), any(Pageable.class))).thenReturn(page);

        MedicalRecordListResponseDTO listDTO = new MedicalRecordListResponseDTO();
        when(medicalRecordMapper.toListResponseDTO(any(MedicalRecord.class))).thenReturn(listDTO);

        Page<MedicalRecordListResponseDTO> result = medicalRecordService.getRecordsByDoctor(1L, 1L, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for getRecordsByDoctor when doctor not found")
    void testGetRecordsByDoctor_DoctorNotFound() {
        when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> medicalRecordService.getRecordsByDoctor(1L, 1L, Pageable.unpaged()));
        assertNotNull(exception.getMessage());
    }

    // ── getRecordsByHospital ─────────────────────────────────────────────────

    @Test
    @DisplayName("Should get records by hospital")
    void testGetRecordsByHospital_Success() {
        Page<MedicalRecord> page = new PageImpl<>(List.of(testRecord));
        when(medicalRecordRepository.findAllByHospitalIdFiltered(eq(1L), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        MedicalRecordListResponseDTO listDTO = new MedicalRecordListResponseDTO();
        when(medicalRecordMapper.toListResponseDTO(any(MedicalRecord.class))).thenReturn(listDTO);

        Page<MedicalRecordListResponseDTO> result = medicalRecordService.getRecordsByHospital(1L, null, null, null, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should throw InvalidRequestException when from date is after to date")
    void testGetRecordsByHospital_InvalidDateRange() {
        java.time.LocalDate from = java.time.LocalDate.of(2026, 9, 20);
        java.time.LocalDate to = java.time.LocalDate.of(2026, 9, 10);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> medicalRecordService.getRecordsByHospital(1L, null, from, to, Pageable.unpaged()));
        assertNotNull(exception.getMessage());
    }

    // ── createRecord additional branches ─────────────────────────────────────

    @Test
    @DisplayName("Should throw InvalidRequestException when appointment patient mismatch")
    void testCreateRecord_PatientMismatch() {
        requestDTO.setAppointmentId(1L);
        Appointment testAppointment = new Appointment();
        testAppointment.setId(1L);
        Patient differentPatient = new Patient();
        differentPatient.setId(99L);
        testAppointment.setPatient(differentPatient);

        when(patientRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testAppointment));
        when(medicalRecordRepository.existsByAppointmentIdAndHospitalId(1L, 1L)).thenReturn(false);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> medicalRecordService.createRecord(1L, requestDTO));
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when patient not found during create")
    void testCreateRecord_PatientNotFound() {
        when(patientRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> medicalRecordService.createRecord(1L, requestDTO));
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when doctor not found during create")
    void testCreateRecord_DoctorNotFound() {
        when(patientRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> medicalRecordService.createRecord(1L, requestDTO));
        assertNotNull(exception.getMessage());
    }

    // ── getRecordsByPatient — patient not found ──────────────────────────────

    @Test
    @DisplayName("Should throw ResourceNotFoundException for getRecordsByPatient when patient not found")
    void testGetRecordsByPatient_PatientNotFound() {
        when(patientRepository.existsByIdAndHospitalId(1L, 1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> medicalRecordService.getRecordsByPatient(1L, 1L, Pageable.unpaged()));
        assertNotNull(exception.getMessage());
    }

    // ── getRecordById — null/zero hospitalId ─────────────────────────────────

    @Test
    @DisplayName("Should get record by ID with null hospitalId (uses findByIdWithDetails)")
    void testGetRecordById_NullHospitalId() {
        when(medicalRecordRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testRecord));
        when(medicalRecordMapper.toResponseDTO(testRecord)).thenReturn(responseDTO);

        MedicalRecordResponseDTO result = medicalRecordService.getRecordById(1L, null);

        assertNotNull(result);
        verify(medicalRecordRepository).findByIdWithDetails(1L);
    }

    // ── deleteRecord — not found ─────────────────────────────────────────────

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent record")
    void testDeleteRecord_NotFound() {
        when(medicalRecordRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> medicalRecordService.deleteRecord(1L, 1L));
        assertNotNull(exception.getMessage());
    }
}
