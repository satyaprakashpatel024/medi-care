package com.care.medi.repository;

import com.care.medi.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

  @EntityGraph(attributePaths = {"doctor", "patient"})
  Page<Prescription> findByPatientId(Long patientId, Pageable pageable);

  @EntityGraph(attributePaths = {"doctor", "patient"})
  Page<Prescription> findByAppointmentId(Long appointmentId, Pageable pageable);
}
