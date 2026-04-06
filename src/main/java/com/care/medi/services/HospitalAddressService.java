package com.care.medi.services;

import com.care.medi.dtos.request.HospitalAddressRequestDTO;
import com.care.medi.dtos.response.HospitalAddressResponseDTO;
import com.care.medi.entity.HospitalAddress;

public interface HospitalAddressService {
    public HospitalAddressResponseDTO getHospitalAddressById(Long id);
    public HospitalAddress createHospitalAddress(Long id, HospitalAddressRequestDTO request);
}
