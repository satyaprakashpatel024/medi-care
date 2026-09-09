package com.care.medi.utils;

import com.care.medi.dtos.response.MedicalRecordListResponseDTO;
import com.care.medi.dtos.response.MedicalRecordResponseDTO;
import com.care.medi.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MedicalRecordMapperTest {

    private MedicalRecordMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MedicalRecordMapper();
    }

    @Test
    @DisplayName("Should map full MedicalRecord to MedicalRecordResponseDTO")
    void testToResponseDTOFull() {
        Patient patient = Patient.builder()
                .firstName("John")
                .lastName("Doe")
                .bloodGroup(BloodGroup.O_POS)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
        patient.setId(1L);

        Department department = Department.builder()
                .name("Cardiology")
                .build();
        department.setId(10L);

        Hospital hospital = Hospital.builder()
                .name("City General")
                .build();
        hospital.setId(100L);

        Doctor doctor = Doctor.builder()
                .firstName("Jane")
                .lastName("Smith")
                .speciality("Cardiologist")
                .department(department)
                .hospital(hospital)
                .build();
        doctor.setId(2L);

        Appointment appointment = Appointment.builder()
                .id(50L)
                .appointmentDate(LocalDate.of(2026, 9, 10))
                .build();

        MedicalRecord record = MedicalRecord.builder()
                .id(5L)
                .patientId(1L)
                .doctorId(2L)
                .appointmentId(50L)
                .hospitalId(100L)
                .patient(patient)
                .doctor(doctor)
                .appointment(appointment)
                .diagnosis("Chest Pain")
                .symptoms("Shortness of breath")
                .treatmentPlan("Rest and Medication")
                .followUpNotes("Check after 2 weeks")
                .recordDate(LocalDate.of(2026, 9, 10))
                .status(RecordStatus.ACTIVE)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        MedicalRecordResponseDTO dto = mapper.toResponseDTO(record);

        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals(1L, dto.getPatientId());
        assertEquals("John Doe", dto.getPatientName());
        assertEquals("O_POS", dto.getPatientBloodGroup());
        assertEquals(2L, dto.getDoctorId());
        assertEquals("Jane Smith", dto.getDoctorName());
        assertEquals("Cardiologist", dto.getDoctorSpeciality());
        assertEquals(10L, dto.getDepartmentId());
        assertEquals("Cardiology", dto.getDepartmentName());
        assertEquals(50L, dto.getAppointmentId());
        assertEquals("2026-09-10", dto.getAppointmentDate());
        assertEquals(100L, dto.getHospitalId());
        assertEquals("City General", dto.getHospitalName());
        assertEquals("Chest Pain", dto.getDiagnosis());
        assertEquals("ACTIVE", dto.getStatus());
    }

    @Test
    @DisplayName("Should map MedicalRecord with null optional references")
    void testToResponseDTONullFields() {
        Patient patient = Patient.builder()
                .firstName("John")
                .lastName("Doe")
                .bloodGroup(null)
                .build();
        patient.setId(1L);

        Doctor doctor = Doctor.builder()
                .firstName("Jane")
                .lastName("Smith")
                .speciality("General")
                .department(null)
                .hospital(null)
                .build();
        doctor.setId(2L);

        MedicalRecord record = MedicalRecord.builder()
                .id(5L)
                .patient(patient)
                .doctor(doctor)
                .appointment(null)
                .hospitalId(100L)
                .diagnosis("Flu")
                .recordDate(LocalDate.now())
                .status(RecordStatus.ACTIVE)
                .build();

        MedicalRecordResponseDTO dto = mapper.toResponseDTO(record);

        assertNotNull(dto);
        assertNull(dto.getPatientBloodGroup());
        assertNull(dto.getDepartmentId());
        assertNull(dto.getDepartmentName());
        assertNull(dto.getAppointmentId());
        assertNull(dto.getAppointmentDate());
        assertNull(dto.getHospitalName());
    }

    @Test
    @DisplayName("Should map to MedicalRecordListResponseDTO with truncated diagnosis when length > 120")
    void testToListResponseDTOTruncatedDiagnosis() {
        Patient patient = Patient.builder().firstName("John").lastName("Doe").build();
        patient.setId(1L);

        Doctor doctor = Doctor.builder().firstName("Jane").lastName("Smith").speciality("Cardiology").build();
        doctor.setId(2L);

        String longDiagnosis = "A".repeat(150);

        MedicalRecord record = MedicalRecord.builder()
                .id(5L)
                .patient(patient)
                .doctor(doctor)
                .diagnosis(longDiagnosis)
                .recordDate(LocalDate.now())
                .status(RecordStatus.ACTIVE)
                .build();

        MedicalRecordListResponseDTO dto = mapper.toListResponseDTO(record);

        assertNotNull(dto);
        assertEquals(121, dto.getDiagnosis().length()); // 120 chars + '…'
        assertTrue(dto.getDiagnosis().endsWith("…"));
    }

    @Test
    @DisplayName("Should map to MedicalRecordListResponseDTO without truncation when diagnosis <= 120")
    void testToListResponseDTONotTruncatedDiagnosis() {
        Patient patient = Patient.builder().firstName("John").lastName("Doe").build();
        patient.setId(1L);

        Doctor doctor = Doctor.builder().firstName("Jane").lastName("Smith").speciality("Cardiology").build();
        doctor.setId(2L);

        String shortDiagnosis = "Mild headache";

        MedicalRecord record = MedicalRecord.builder()
                .id(5L)
                .patient(patient)
                .doctor(doctor)
                .diagnosis(shortDiagnosis)
                .recordDate(LocalDate.now())
                .status(RecordStatus.ACTIVE)
                .build();

        MedicalRecordListResponseDTO dto = mapper.toListResponseDTO(record);

        assertNotNull(dto);
        assertEquals("Mild headache", dto.getDiagnosis());
    }
}
