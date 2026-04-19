package com.care.medi.dtos.response;

import com.care.medi.entity.Hospital;
import lombok.Builder;

import java.util.Set;

@Builder
public record HospitalListResponseDTO(
        Long id,
        String name,
        String phone,
        Set<HospitalAddressResponseDTO> address
) {
    public static  HospitalListResponseDTO fromEntity(Hospital hospital) {
        return HospitalListResponseDTO.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .phone(hospital.getPhone())
                .address(HospitalAddressResponseDTO.fromEntity(hospital.getAddresses()))
                .build();
    }
}
