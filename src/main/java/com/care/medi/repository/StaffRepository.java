package com.care.medi.repository;

import com.care.medi.entity.Staff;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    @EntityGraph(attributePaths = {"user", "hospital"})
    @Query("SELECT s FROM Staff s WHERE s.hospital.id = :hospitalId")
    List<Staff> findByHospitalId(Long hospitalId);
}