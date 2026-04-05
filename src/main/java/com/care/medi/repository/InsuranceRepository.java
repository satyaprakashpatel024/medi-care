package com.care.medi.repository;

import com.care.medi.entity.Insurance;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {

    @Override
    @NonNull
    Page<Insurance> findAll(@NonNull Pageable pageable);

    List<Insurance> findByPatientId(Long patientId);
}