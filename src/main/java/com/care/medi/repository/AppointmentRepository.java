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
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"patient", "department", "doctor"})
    Optional<Appointment> findById(Long id);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByHospitalIdAndAppointmentDateBetween(Long hospitalId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByHospitalAndStatusAndAppointmentDateBetween(Long hospitalId, AppointmentStatus status, LocalDateTime start, LocalDateTime end, Pageable pageable);

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
            """)
    Page<AppointmentListResponseDTO> findByDoctorId(Long doctorId, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByHospitalAndPatient(Long hospitalId,Long patientId, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByDepartmentIdAndAppointmentDateBetween(Long departmentId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByHospitalId(Long hospitalId,Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByDoctorIdAndAppointmentDateBetween(Long doctorId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByDepartmentIdAndStatusAndAppointmentDateBetween(Long departmentId, AppointmentStatus status, LocalDateTime start,LocalDateTime end, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "department", "doctor", "prescription"})
    Page<Appointment> findByHospitalIdAndStatusAndAppointmentDateBetween(Long hospitalId, AppointmentStatus status, LocalDateTime start, LocalDateTime end, Pageable pageable);
}