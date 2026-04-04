package com.care.medi.repository;

import com.care.medi.entity.Doctor;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("SELECT d FROM Doctor d WHERE d.hospital.id = :hospitalId")
    List<Doctor> findByHospitalId(Long hospitalId);

    @Query("SELECT d FROM Doctor d WHERE d.department.id = :departmentId")
    List<Doctor> findByDepartmentId(Long departmentId);

    @Query("SELECT d FROM Doctor d WHERE d.speciality = :speciality")
    List<Doctor> findBySpeciality(String speciality);

    boolean existsByUserId(@NotNull Long userId);

    @Query("SELECT d FROM Doctor d JOIN FETCH d.hospital JOIN FETCH d.department")
    List<Doctor> findAllWithDetails();

    @Query("SELECT d FROM Doctor d JOIN FETCH d.hospital JOIN FETCH d.department WHERE d.hospital.id = :hospitalId")
    List<Doctor> findByHospitalIdWithDetails(Long hospitalId);

    @Query("SELECT d FROM Doctor d JOIN FETCH d.hospital JOIN FETCH d.department WHERE d.id = :id")
    Optional<Doctor> findByIdWithDetails(Long id);

    @Query("SELECT d FROM Doctor d JOIN FETCH d.hospital JOIN FETCH d.department WHERE d.department.id = :departmentId")
    List<Doctor> findByDepartmentIdWithDetails(Long departmentId);

    @Query("SELECT d FROM Doctor d WHERE d.speciality LIKE CONCAT('%', :speciality, '%')")
    List<Doctor> findBySpecialityIgnoreCaseWithDetails(String speciality);
}