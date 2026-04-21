package com.care.medi.services;

import com.care.medi.dtos.request.MedicalRecordRequestDTO;
import com.care.medi.dtos.request.MedicalRecordUpdateRequestDTO;
import com.care.medi.dtos.response.MedicalRecordListResponseDTO;
import com.care.medi.dtos.response.MedicalRecordResponseDTO;
import com.care.medi.entity.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface MedicalRecordService {

    /** Create a new medical record, optionally linked to an appointment. */
    MedicalRecordResponseDTO createRecord(Long hospitalId, MedicalRecordRequestDTO request);

    /** Fetch a single record by ID (scoped to hospital when hospitalId > 0). */
    MedicalRecordResponseDTO getRecordById(Long id, Long hospitalId);

    /** Fetch the record linked to a specific appointment. */
    MedicalRecordResponseDTO getRecordByAppointmentId(Long appointmentId, Long hospitalId);

    /** Paginated history for a patient within a hospital. */
    Page<MedicalRecordListResponseDTO> getRecordsByPatient(
            Long patientId, Long hospitalId, Pageable pageable);

    /** Active records only for a patient (excludes ARCHIVED). */
    Page<MedicalRecordListResponseDTO> getActiveRecordsByPatient(
            Long patientId, Long hospitalId, Pageable pageable);

    /** Most recent active record for a patient. */
    MedicalRecordResponseDTO getLatestRecordByPatient(Long patientId, Long hospitalId);

    /** Paginated records authored by a doctor within a hospital. */
    Page<MedicalRecordListResponseDTO> getRecordsByDoctor(
            Long doctorId, Long hospitalId, Pageable pageable);

    /** Hospital-wide filtered list (status, date range). */
    Page<MedicalRecordListResponseDTO> getRecordsByHospital(
            Long hospitalId, RecordStatus status,
            LocalDate from, LocalDate to, Pageable pageable);

    /** Update clinical content or archive a record. */
    MedicalRecordResponseDTO updateRecord(Long id, Long hospitalId, MedicalRecordUpdateRequestDTO request);

    /** Hard delete — only permitted when no appointment is linked. */
    String deleteRecord(Long id, Long hospitalId);
}
