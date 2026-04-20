package com.care.medi.repository;

import com.care.medi.dtos.response.PrescriptionResponseDTO;
import com.care.medi.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    @Query(
            """
        SELECT new com.care.medi.dtos.response.PrescriptionResponseDTO(
                p.id,
                concat(p.patient.firstName, ' ', p.patient.lastName),
                d.id,
                concat(d.firstName, ' ', d.lastName),
                p.medications,
                p.dosageInstructions,
                p.notes
                )
                FROM Prescription p
                JOIN p.doctor d
                WHERE p.patient.id = :patientId
        """
    )
    Page<PrescriptionResponseDTO> findByPatientId(Long patientId, Pageable pageable);

    Page<Prescription> findByAppointmentId(Long appointmentId, Pageable pageable);
}