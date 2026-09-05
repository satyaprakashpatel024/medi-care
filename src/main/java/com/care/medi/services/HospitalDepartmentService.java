package com.care.medi.services;

import com.care.medi.dtos.response.HospitalDepartmentResponseDTO;

import java.util.List;

public interface HospitalDepartmentService {
    List<HospitalDepartmentResponseDTO> findAll();

    HospitalDepartmentResponseDTO mapDepartmentToHospital(Long hospitalId, Long departmentId);

    void unmapDepartmentFromHospital(Long hospitalId, Long departmentId);
}
