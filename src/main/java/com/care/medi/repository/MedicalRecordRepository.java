package com.care.medi.repository;

import com.care.medi.entity.MedicalRecord;
import com.care.medi.entity.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    // -------------------------------------------------------------------------
    // Single-record lookups
    // -------------------------------------------------------------------------

    /**
     * Fetch a record by ID scoped to a hospital (multi-tenant safe).
     */
    @Query("SELECT mr FROM MedicalRecord mr " +
            "JOIN FETCH mr.patient p " +
            "JOIN FETCH mr.doctor d " +
            "LEFT JOIN FETCH mr.appointment a " +
            "WHERE mr.id = :id AND mr.hospitalId = :hospitalId")
    Optional<MedicalRecord> findByIdAndHospitalId(
            @Param("id") Long id,
            @Param("hospitalId") Long hospitalId);

    /**
     * Cross-hospital lookup (super-admin use).
     */
    @Query("SELECT mr FROM MedicalRecord mr " +
            "JOIN FETCH mr.patient p " +
            "JOIN FETCH mr.doctor d " +
            "LEFT JOIN FETCH mr.appointment a " +
            "WHERE mr.id = :id")
    Optional<MedicalRecord> findByIdWithDetails(@Param("id") Long id);

    /**
     * Fetch the record linked to a specific appointment.
     */
    @Query("SELECT mr FROM MedicalRecord mr " +
            "JOIN FETCH mr.patient p " +
            "JOIN FETCH mr.doctor d " +
            "WHERE mr.appointment.id = :appointmentId")
    Optional<MedicalRecord> findByAppointmentId(@Param("appointmentId") Long appointmentId);

    // -------------------------------------------------------------------------
    // Patient-scoped list queries
    // -------------------------------------------------------------------------

    /**
     * All records for a patient within a hospital, newest first.
     */
    @Query("SELECT mr FROM MedicalRecord mr " +
            "JOIN FETCH mr.doctor d " +
            "LEFT JOIN FETCH mr.appointment a " +
            "WHERE mr.patient.id = :patientId AND mr.hospitalId = :hospitalId " +
            "ORDER BY mr.recordDate DESC, mr.createdAt DESC")
    Page<MedicalRecord> findAllByPatientIdAndHospitalId(
            @Param("patientId") Long patientId,
            @Param("hospitalId") Long hospitalId,
            Pageable pageable);

    /**
     * Active records only for a patient.
     */
    @Query("SELECT mr FROM MedicalRecord mr " +
            "JOIN FETCH mr.doctor d " +
            "WHERE mr.patient.id = :patientId AND mr.hospitalId = :hospitalId " +
            "AND mr.status = 'ACTIVE' " +
            "ORDER BY mr.recordDate DESC")
    Page<MedicalRecord> findActiveByPatientIdAndHospitalId(
            @Param("patientId") Long patientId,
            @Param("hospitalId") Long hospitalId,
            Pageable pageable);

    /**
     * Most recent active record for a patient (for the /latest endpoint).
     */
    @Query("SELECT mr FROM MedicalRecord mr " +
            "JOIN FETCH mr.doctor d " +
            "LEFT JOIN FETCH mr.appointment a " +
            "WHERE mr.patient.id = :patientId AND mr.hospitalId = :hospitalId " +
            "AND mr.status = 'ACTIVE' " +
            "ORDER BY mr.recordDate DESC, mr.createdAt DESC")
    Page<MedicalRecord> findLatestActiveByPatientIdAndHospitalId(
            @Param("patientId") Long patientId,
            @Param("hospitalId") Long hospitalId,
            Pageable pageable);

    // -------------------------------------------------------------------------
    // Doctor-scoped list queries
    // -------------------------------------------------------------------------

    /**
     * All records written by a doctor within a hospital.
     */
    @Query("SELECT mr FROM MedicalRecord mr " +
            "JOIN FETCH mr.patient p " +
            "LEFT JOIN FETCH mr.appointment a " +
            "WHERE mr.doctor.id = :doctorId AND mr.hospitalId = :hospitalId " +
            "ORDER BY mr.recordDate DESC")
    Page<MedicalRecord> findAllByDoctorIdAndHospitalId(
            @Param("doctorId") Long doctorId,
            @Param("hospitalId") Long hospitalId,
            Pageable pageable);

    // -------------------------------------------------------------------------
    // Date-range + status filter query (hospital-wide)
    // -------------------------------------------------------------------------

    @Query("SELECT mr FROM MedicalRecord mr " +
            "JOIN FETCH mr.patient p " +
            "JOIN FETCH mr.doctor d " +
            "WHERE mr.hospitalId = :hospitalId " +
            "AND (:status IS NULL OR mr.status = :status) " +
            "AND (:from IS NULL OR mr.recordDate >= :from) " +
            "AND (:to   IS NULL OR mr.recordDate <= :to) " +
            "ORDER BY mr.recordDate DESC")
    Page<MedicalRecord> findAllByHospitalIdFiltered(
            @Param("hospitalId") Long hospitalId,
            @Param("status") RecordStatus status,
            @Param("from")       LocalDate from,
            @Param("to")         LocalDate to,
            Pageable pageable);

    // -------------------------------------------------------------------------
    // Existence checks
    // -------------------------------------------------------------------------

    boolean existsByAppointmentIdAndHospitalId(Long appointmentId, Long hospitalId);

    boolean existsByIdAndHospitalId(Long id, Long hospitalId);
}
