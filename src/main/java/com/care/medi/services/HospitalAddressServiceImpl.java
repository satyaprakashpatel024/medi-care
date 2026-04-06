package com.care.medi.services;

import com.care.medi.dtos.request.HospitalAddressRequestDTO;
import com.care.medi.dtos.response.HospitalAddressResponseDTO;
import com.care.medi.entity.Hospital;
import com.care.medi.entity.HospitalAddress;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.HospitalAddressRepository;
import com.care.medi.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HospitalAddressServiceImpl implements HospitalAddressService {
    private final HospitalAddressRepository hospitalAddressRepository;
    private final HospitalRepository hospitalRepository;

    public HospitalAddressResponseDTO getHospitalAddressById(Long id) {
        return toHospitalAddressResponse(
                hospitalAddressRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("Hospital Address with id %d not found", id))));
    }

    public HospitalAddress createHospitalAddress(Long id, HospitalAddressRequestDTO request) {
        return hospitalAddressRepository.save(toHospitalAddressEntity(id, request));
    }

    private HospitalAddress toHospitalAddressEntity(Long id, HospitalAddressRequestDTO request) {
        Hospital hospital = hospitalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Hospital with id %d not found", id)));
        return HospitalAddress.builder()
                .hospital(hospital)
                .phoneNumber(request.getPhoneNumber())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .landmark(request.getLandmark())
                .build();
    }


    public HospitalAddressResponseDTO toHospitalAddressResponse(HospitalAddress address) {
        return HospitalAddressResponseDTO.builder()
                .id(address.getId())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .landmark(address.getLandmark())
                .phoneNumber(address.getPhoneNumber())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
