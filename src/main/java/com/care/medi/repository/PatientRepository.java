package com.care.medi.repository;

import com.care.medi.dtos.response.PatientListResponseDTO;
import com.care.medi.entity.Patient;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"user"})
    Page<Patient> findAll(Pageable pageable);


    @NonNull
    @EntityGraph(attributePaths = {"user", "insurances"})
    Optional<Patient> findByIdAndHospitalId(@NonNull Long id, @NonNull Long hospitalId);

    @Query("SELECT DISTINCT new com.care.medi.dtos.response.PatientListResponseDTO(" +
            "p.id, p.user.id, p.firstName, p.lastName, p.dateOfBirth, " +
            "CAST(p.gender AS string), p.phone, p.emergencyContact, " +
            "CAST(p.bloodGroup AS string), p.createdAt) " +
            "FROM Patient p " +
            "JOIN p.appointments a " +
            "WHERE a.hospital.id = :hospitalId")
    Page<PatientListResponseDTO> findAllByHospitalId(
            @Param("hospitalId") Long hospitalId,
            Pageable pageable
    );

    void deleteByIdAndHospitalId(Long patientId, Long hospitalId);
}