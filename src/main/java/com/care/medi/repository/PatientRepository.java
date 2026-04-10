package com.care.medi.repository;

import com.care.medi.entity.Patient;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"user"})
    Page<Patient> findAll(Pageable pageable);

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"user","insurances"})
    Optional<Patient> findById(@NonNull Long id);
}