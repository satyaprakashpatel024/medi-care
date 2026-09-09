package com.care.medi.services;

import com.care.medi.dtos.request.MedicalRecordRequestDTO;
import com.care.medi.dtos.request.MedicalRecordUpdateRequestDTO;
import com.care.medi.dtos.response.MedicalRecordListResponseDTO;
import com.care.medi.dtos.response.MedicalRecordResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.AppointmentRepository;
import com.care.medi.repository.DoctorRepository;
import com.care.medi.repository.MedicalRecordRepository;
import com.care.medi.repository.PatientRepository;
import com.care.medi.utils.MedicalRecordMapper;
import org.apache.coyote.BadRequestException;
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
    void testCreateRecord() throws Exception {
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
    @DisplayName("Should throw BadRequestException if appointment already has a record")
    void testCreateRecord_DuplicateAppointment() throws Exception {
        requestDTO.setAppointmentId(1L);
        Appointment testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setPatient(testPatient);

        when(patientRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testAppointment));
        when(medicalRecordRepository.existsByAppointmentIdAndHospitalId(1L, 1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> medicalRecordService.createRecord(1L, requestDTO));
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

        assertThrows(ResourceNotFoundException.class, () -> medicalRecordService.getRecordById(1L, 1L));
    }

    @Test
    @DisplayName("Should delete record without appointment")
    void testDeleteRecord() throws Exception {
        when(medicalRecordRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testRecord));

        String result = medicalRecordService.deleteRecord(1L, 1L);

        assertEquals("Medical record ID 1 has been deleted successfully.", result);
        verify(medicalRecordRepository).delete(testRecord);
    }

    @Test
    @DisplayName("Should throw BadRequestException when deleting record with appointment")
    void testDeleteRecord_WithAppointment() {
        Appointment appt = new Appointment();
        appt.setId(2L);
        testRecord.setAppointment(appt);
        when(medicalRecordRepository.findByIdAndHospitalId(1L, 1L)).thenReturn(Optional.of(testRecord));

        assertThrows(BadRequestException.class, () -> medicalRecordService.deleteRecord(1L, 1L));
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
}
