package com.care.medi.repository;

import com.care.medi.entity.DispenseRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispenseRecordRepository extends JpaRepository<DispenseRecord, Long> {
    Page<DispenseRecord> findByPatientId(Long patientId, Pageable pageable);
}
