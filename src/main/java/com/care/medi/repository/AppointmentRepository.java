package com.care.medi.repository;

import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.entity.Appointment;
import com.care.medi.entity.AppointmentStatus;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByHospitalIdAndAppointmentDateBetween(Long hospitalId, ZonedDateTime start, ZonedDateTime end, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByHospitalIdAndStatusAndAppointmentDateBetween(Long hospitalId, AppointmentStatus status, ZonedDateTime start, ZonedDateTime end, Pageable pageable);

    @Query("""
    SELECT new com.care.medi.dtos.response.AppointmentListResponseDTO(
        a.id,
        concat(p.firstName, ' ', p.lastName),
        concat(d.firstName, ' ', d.lastName),
        dept.name,
        a.appointmentDate,
        a.status
    )
    FROM Appointment a
    JOIN a.patient p
    JOIN a.doctor d
    JOIN d.department dept
    WHERE d.id = :doctorId
    AND d.hospital.id = :hospitalId
""")
    Page<AppointmentListResponseDTO> findByDoctorIdAndHospitalId(@Param("doctorId") Long doctorId, @Param("hospitalId") Long hospitalId, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByHospitalIdAndPatientId(@Param("hospitalId") Long hospitalId, @Param("patientId") Long patientId, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByDepartmentIdAndAppointmentDateBetween(@Param("departmentId") Long departmentId, ZonedDateTime start, ZonedDateTime end, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByHospitalId(@Param("hospitalId") Long hospitalId, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByDoctorIdAndStatus(@Param("doctorId") Long doctorId, AppointmentStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByPatientIdAndStatus(@Param("patientId") Long patientId, AppointmentStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByDoctorIdAndAppointmentDateBetween(@Param("doctorId") Long doctorId, ZonedDateTime start, ZonedDateTime end, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByDepartmentIdAndStatusAndAppointmentDateBetween(@Param("departmentId") Long departmentId, AppointmentStatus status, ZonedDateTime start, ZonedDateTime end, Pageable pageable);

    @NonNull
    @EntityGraph(attributePaths = {"patient", "department", "doctor"})
    Optional<Appointment> findByIdAndHospitalId(@Param("id") Long id,@Param("hospitalId") Long hospitalId);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByPatientIdAndAppointmentDateBetween(@Param("patientId") Long patientId, ZonedDateTime start, ZonedDateTime end, Pageable pageable);
}