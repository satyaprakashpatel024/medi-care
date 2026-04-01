package com.care.medi.repository;

import com.care.medi.entity.Staff;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    @EntityGraph(attributePaths = {"user", "hospital"})
    List<Staff> findByHospitalId(Long hospitalId);
}