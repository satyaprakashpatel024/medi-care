package com.care.medi.repository;

import com.care.medi.entity.Doctor;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"hospital", "department"})
    Page<Doctor> findAll(@NonNull Pageable pageable);

    @Override @NonNull
    @EntityGraph(attributePaths = {"user", "user.addresses", "hospital", "department"})
    Optional<Doctor> findById(@NonNull Long id);

    boolean existsByUserId(@NotNull Long userId);

    @EntityGraph(attributePaths = {"hospital", "department"})
    Page<Doctor> findByHospitalId(Long hospitalId,Pageable pageable);

    @Query("SELECT d FROM Doctor d JOIN FETCH d.hospital JOIN FETCH d.department WHERE d.id = :id")
    Optional<Doctor> findByIdWithDetails(Long id);

    @EntityGraph(attributePaths = {"hospital", "department"})
    Page<Doctor> findByDepartmentIdAndHospitalId(Long hospitalId ,Long departmentId,Pageable pageable);

    @EntityGraph(attributePaths = {"hospital", "department"})
    Page<Doctor> findBySpecialityContainingIgnoreCase(String speciality, Pageable pageable);
}