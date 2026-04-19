package com.care.medi.services;

import com.care.medi.dtos.request.DepartmentRequestDTO;
import com.care.medi.dtos.response.DepartmentResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;


public interface DepartmentService {

    Page<DepartmentResponseDTO> getAllDepartments(int page, int size, String sortBy);

    DepartmentResponseDTO createDepartment(@Valid DepartmentRequestDTO departmentRequestDTO);

    DepartmentResponseDTO getDepartmentById(Long id);

    DepartmentResponseDTO updateDepartment(Long id, @Valid DepartmentRequestDTO request);
}
