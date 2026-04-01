package com.care.medi.repository;

import com.care.medi.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByHospitalId(Long hospitalId);

//    Optional<Doctor> findByIdWithUpcomingAppointments(Long id);

    List<Doctor> findBySpeciality(String speciality);
}