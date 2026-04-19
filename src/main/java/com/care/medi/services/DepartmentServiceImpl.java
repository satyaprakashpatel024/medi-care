package com.care.medi.services;

import com.care.medi.dtos.request.DepartmentRequestDTO;
import com.care.medi.dtos.response.DepartmentResponseDTO;
import com.care.medi.entity.Department;
import com.care.medi.exception.DuplicateResourceException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
//    private final

    @Override
    public Page<DepartmentResponseDTO> getAllDepartments(int page,int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Department> all = departmentRepository.findAll(pageable);
        return all.map(DepartmentResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO departmentRequestDTO) {
        if (departmentRepository.existsByName(departmentRequestDTO.getName())) {
            throw new DuplicateResourceException("Department already exists with this name.");
        }
        Department department = Department.builder()
                .name(departmentRequestDTO.getName())
                .description(departmentRequestDTO.getDescription())
                .build();
        department = departmentRepository.save(department);
        return DepartmentResponseDTO.fromEntity(department);
    }

    @Override
    public DepartmentResponseDTO getDepartmentById(Long id) {
        Optional<Department> byId = departmentRepository.findById(id);
        if (byId.isPresent()){
            return DepartmentResponseDTO.fromEntity(byId.get());
        }
        throw  new ResourceNotFoundException(STR."Department with id: \{id} not found.");
    }

    @Override
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO request) {
        Department byId = departmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department with id: " + id + " not found."));
        if (request.getName()!=null ) {
            byId.setName(request.getName());
        }
        if (request.getDescription()!=null ) {
            byId.setDescription(request.getDescription());
        }
        departmentRepository.saveAndFlush(byId);
        return DepartmentResponseDTO.fromEntity(byId);
    }

}
