package com.care.medi.services;

import com.care.medi.dtos.request.InsuranceRequestDTO;
import com.care.medi.dtos.response.InsuranceResponseDTO;

public interface InsuranceService {
    InsuranceResponseDTO createInsurance(InsuranceRequestDTO request);
    InsuranceResponseDTO updateInsurance(Long id, InsuranceRequestDTO request);
    void deleteInsurance(Long id);
    InsuranceResponseDTO getInsuranceByHospitalId(Long hospitalId);
    InsuranceResponseDTO getInsuranceByUserId(Long userId);
    InsuranceResponseDTO getInsuranceByPolicyNumber(String policyNumber);
}
