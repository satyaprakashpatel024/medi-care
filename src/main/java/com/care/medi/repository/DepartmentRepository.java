package com.care.medi.repository;

import com.care.medi.entity.Department;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(@NotBlank(message = "Department Name is required.") @Size(max = 150) String name);
}