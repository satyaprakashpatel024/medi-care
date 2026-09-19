package com.care.medi.repository;

import com.care.medi.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
  List<Medication> findByStockQuantityLessThanEqual(Integer threshold);
}
