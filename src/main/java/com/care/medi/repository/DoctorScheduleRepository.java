package com.care.medi.repository;

import com.care.medi.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

  Optional<DoctorSchedule> findByDoctorIdAndHospitalIdAndIsActiveTrue(Long doctorId, Long hospitalId);

  Optional<DoctorSchedule> findByDoctorId(Long doctorId);

  boolean existsByDoctorIdAndHospitalId(Long doctorId, Long hospitalId);
}
