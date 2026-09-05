package com.care.medi.repository;

import com.care.medi.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    @EntityGraph(attributePaths = {"user", "hospital"})
    @Query("SELECT s FROM Staff s WHERE s.hospital.id = :hospitalId")
    List<Staff> findByHospitalId(Long hospitalId);

    @EntityGraph(attributePaths = {"user", "hospital"})
    Page<Staff> findByHospitalId(Long hospitalId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "hospital"})
    Page<Staff> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "hospital"})
    @Query("SELECT s FROM Staff s WHERE s.user.id = :userId")
    java.util.Optional<Staff> findByUserId(Long userId);

    @Query(value = "SELECT s.hospital_id FROM staffs s WHERE s.user_id = :userId", nativeQuery = true)
    Optional<Long> findHospitalIdByUserId(@Param("userId") Long userId);

    long countByHospitalId(Long hospitalId);
}