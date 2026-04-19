package com.care.medi.repository;

import com.care.medi.entity.Insurance;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {

    @Override
    @NonNull
    Page<Insurance> findAll(@NonNull Pageable pageable);

    List<Insurance> findByPatientId(Long patientId);

    Optional<Insurance> findByPolicyNumber(@NotBlank(message = "Policy number is required") String policyNumber);

    boolean existsByPolicyNumber(@NotBlank(message = "Policy number is required") String policyNumber);

}