package com.care.medi.repository;

import com.care.medi.entity.HospitalDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalDepartmentRepository extends JpaRepository<HospitalDepartment, Long> {
    @Query("SELECT hd FROM HospitalDepartment hd WHERE hd.hospital.id = :hospitalId AND hd.department.id = :departmentId")
    boolean existsByHospitalIdAndDepartmentId(Long hospitalId, Long departmentId);

    @Query("SELECT hd FROM HospitalDepartment hd WHERE hd.hospital.id = :hospitalId AND hd.department.id = :departmentId")
    Optional<HospitalDepartment> findByHospitalIdAndDepartmentId(Long hospitalId, Long departmentId);
}