package com.care.medi.services;

import com.care.medi.dtos.response.HospitalDepartmentResponseDTO;

import java.util.List;

public interface HospitalDepartmentService {
    List<HospitalDepartmentResponseDTO> findAll();
}
