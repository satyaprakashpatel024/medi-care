package com.care.medi.repository;

import com.care.medi.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
//    Optional<Patient> findByIdWithAppointments(Long id);
}