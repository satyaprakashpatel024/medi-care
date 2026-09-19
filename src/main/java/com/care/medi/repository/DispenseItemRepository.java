package com.care.medi.repository;

import com.care.medi.entity.DispenseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispenseItemRepository extends JpaRepository<DispenseItem, Long> {
}
