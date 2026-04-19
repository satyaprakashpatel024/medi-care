package com.care.medi.services;

import com.care.medi.dtos.request.HospitalAddressRequestDTO;
import com.care.medi.dtos.response.HospitalAddressResponseDTO;
import com.care.medi.entity.Hospital;
import com.care.medi.entity.HospitalAddress;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.HospitalAddressRepository;
import com.care.medi.repository.HospitalRepository;
import com.care.medi.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HospitalAddressServiceImpl implements HospitalAddressService {
    private final HospitalAddressRepository hospitalAddressRepository;
    private final HospitalRepository hospitalRepository;

    public HospitalAddressResponseDTO getHospitalAddressById(Long id) {
        HospitalAddress hospitalAddress = hospitalAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Hospital Address with id %d not found", id)));
        return HospitalAddressResponseDTO.fromEntity(hospitalAddress);
    }

    public HospitalAddress createHospitalAddress(Long id, HospitalAddressRequestDTO request) {
        Hospital hospital = hospitalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + id));
        HospitalAddress entity = HospitalAddress.toEntity(hospital, request);
        return hospitalAddressRepository.save(entity);
    }

}
