package com.care.medi.services;

import com.care.medi.dtos.request.InsuranceRequestDTO;
import com.care.medi.dtos.response.InsuranceResponseDTO;
import com.care.medi.entity.Insurance;
import com.care.medi.repository.InsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InsuranceServiceImpl implements InsuranceService {

    private final InsuranceRepository insuranceRepository;

    @Override
    public InsuranceResponseDTO createInsurance(InsuranceRequestDTO request) {
        return null;
    }


    @Override
    public InsuranceResponseDTO updateInsurance(Long id, InsuranceRequestDTO request) {
        return null;
    }

    @Override
    public void deleteInsurance(Long id) {
        throw new NotImplementedException("Not Implemented yet.");
    }

    @Override
    public InsuranceResponseDTO getInsuranceByHospitalId(Long hospitalId) {
        return null;
    }

    @Override
    public InsuranceResponseDTO getInsuranceByUserId(Long userId) {
        return null;
    }

    @Override
    public InsuranceResponseDTO getInsuranceByPolicyNumber(String policyNumber) {
        Optional<Insurance> byPolicyNumber = insuranceRepository.findByPolicyNumber(policyNumber);
        return byPolicyNumber.map(InsuranceResponseDTO::fromEntity).orElse(null);
    }
}
