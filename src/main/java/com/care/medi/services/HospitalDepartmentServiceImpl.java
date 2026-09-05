package com.care.medi.services;

import com.care.medi.dtos.response.HospitalDepartmentResponseDTO;
import com.care.medi.entity.Department;
import com.care.medi.entity.Hospital;
import com.care.medi.entity.HospitalDepartment;
import com.care.medi.exception.DuplicateResourceException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.DepartmentRepository;
import com.care.medi.repository.HospitalDepartmentRepository;
import com.care.medi.repository.HospitalRepository;
import com.care.medi.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalDepartmentServiceImpl implements HospitalDepartmentService {

    private final HospitalDepartmentRepository hospitalDepartmentRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public List<HospitalDepartmentResponseDTO> findAll() {
        return hospitalDepartmentRepository.findAll().stream()
                .map(HospitalDepartmentResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public HospitalDepartmentResponseDTO mapDepartmentToHospital(Long hospitalId, Long departmentId) {
        if (hospitalDepartmentRepository.existsByHospitalIdAndDepartmentId(hospitalId, departmentId)) {
            throw new DuplicateResourceException("Department ID " + departmentId + " is already mapped to Hospital ID " + hospitalId);
        }

        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + hospitalId));

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.DEPARTMENT_NOT_FOUND + departmentId));

        HospitalDepartment hd = HospitalDepartment.builder()
                .hospital(hospital)
                .department(department)
                .active(true)
                .build();

        return HospitalDepartmentResponseDTO.fromEntity(hospitalDepartmentRepository.save(hd));
    }

    @Override
    @Transactional
    public void unmapDepartmentFromHospital(Long hospitalId, Long departmentId) {
        HospitalDepartment hd = hospitalDepartmentRepository.findByHospitalIdAndDepartmentId(hospitalId, departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No mapping found between Hospital ID " + hospitalId + " and Department ID " + departmentId));

        hospitalDepartmentRepository.delete(hd);
    }
}
