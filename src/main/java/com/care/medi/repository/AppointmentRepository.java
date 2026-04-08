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

import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"patient","department","doctor"})
    Optional<Appointment> findById(Long id);

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"patient","department","doctor","prescription"})
    Page<Appointment> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"patient","department","doctor","prescription"})
    Page<Appointment> findByStatus(AppointmentStatus status,Pageable pageable);

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

//    Page<Appointment> findByPatientId(Long patientId,Pageable pageable);
//
//    Page<Appointment> findByDepartmentId(Long departmentId,Pageable pageable);
//
//    Page<Appointment> findByHospitalId(Long hospitalId,Pageable pageable);
//
//    Page<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status, Pageable pageable);
//
//    Page<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status, Pageable pageable);
//
//    Page<Appointment> findByDepartmentIdAndStatus(Long departmentId, AppointmentStatus status, Pageable pageable);
//
//    Page<Appointment> findByHospitalIdAndStatus(Long hospitalId, AppointmentStatus status, Pageable pageable);
//
//    Page<Appointment> findByDoctorIdAndStatusAndAppointmentDate(Long doctorId, AppointmentStatus status, LocalDateTime appointmentDate, Pageable pageable);
//
//    Page<Appointment> findByPatientIdAndStatusAndAppointmentDate(Long patientId, AppointmentStatus status, LocalDateTime appointmentDate, Pageable pageable);
//
//    Page<Appointment> findByDepartmentIdAndStatusAndAppointmentDate(Long departmentId, AppointmentStatus status, LocalDateTime appointmentDate, Pageable pageable);
//
//    Page<Appointment> findByHospitalIdAndStatusAndAppointmentDate(Long hospitalId, AppointmentStatus status, LocalDateTime appointmentDate, Pageable pageable);
//
//    Page<Appointment> findByDoctorIdAndStatusAndAppointmentDateAndPatientId(Long doctorId, AppointmentStatus status, LocalDateTime appointmentDate, Long patientId, Pageable pageable);

}