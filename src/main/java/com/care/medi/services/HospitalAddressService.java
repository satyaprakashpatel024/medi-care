package com.care.medi.services;

import com.care.medi.dtos.request.HospitalAddressRequestDTO;
import com.care.medi.dtos.response.HospitalAddressResponseDTO;
import com.care.medi.entity.HospitalAddress;

public interface HospitalAddressService {
    HospitalAddressResponseDTO getHospitalAddressById(Long id);

    HospitalAddress createHospitalAddress(Long id, HospitalAddressRequestDTO request);
}
