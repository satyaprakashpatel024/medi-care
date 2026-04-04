package com.care.medi.services;

import com.care.medi.dtos.request.HospitalRequestDTO;
import com.care.medi.dtos.response.HospitalResponseDTO;
import com.care.medi.dtos.request.HospitalUpdateRequestDTO;
import com.care.medi.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface HospitalService {
    HospitalResponseDTO createHospital(HospitalRequestDTO request);
    HospitalResponseDTO getHospitalById(Long id);
    Page<HospitalResponseDTO> getAllHospitals(int page, int size, String sortBy);
    HospitalResponseDTO updateHospital(Long id, HospitalUpdateRequestDTO request);
    void assignDepartment(Long hospitalId, Long departmentId) throws BusinessException;
    @Transactional
    void removeDepartment(Long hospitalId, Long departmentId) throws BusinessException;
    void deleteHospital(Long id);
}