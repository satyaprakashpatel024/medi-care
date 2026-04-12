package com.care.medi.repository;

import com.care.medi.entity.Doctor;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @NonNull
    @EntityGraph(attributePaths = {"hospital", "department"})
    Page<Doctor> findByIsActiveTrue(Pageable pageable);

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"hospital", "department"})
    Page<Doctor> findAll(@NonNull Pageable pageable);

    @NonNull
    @EntityGraph(attributePaths = {"user", "hospital", "department"})
    Optional<Doctor> findByIdAndIsActiveTrue(@NonNull Long id);

    @EntityGraph(attributePaths = {"hospital", "department"})
    Page<Doctor> findByHospitalIdAndIsActiveTrue(Long hospitalId, Pageable pageable);

    @EntityGraph(attributePaths = {"hospital", "department"})
    Page<Doctor> findByDepartmentIdAndHospitalIdAndIsActiveTrue(Long hospitalId, Long departmentId, Pageable pageable);

    @EntityGraph(attributePaths = {"hospital", "department"})
    Page<Doctor> findBySpecialityContainingIgnoreCaseAndIsActiveTrue(String speciality, Pageable pageable);
}