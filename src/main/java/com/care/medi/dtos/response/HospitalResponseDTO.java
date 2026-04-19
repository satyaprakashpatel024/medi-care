package com.care.medi.dtos.response;

import com.care.medi.entity.Hospital;
import com.care.medi.entity.HospitalDepartment;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Set;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record HospitalResponseDTO(
        Long id,
        String name,
        String phone,
        Set<HospitalAddressResponseDTO> address,
        Set<HospitalDepartmentResponseDTO> departments
) {
    public static HospitalResponseDTO fromEntity(Hospital entity) {
        return HospitalResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .address(HospitalAddressResponseDTO.fromEntity(entity.getAddresses()))
                .departments(HospitalDepartmentResponseDTO.fromEntity(entity.getHospitalDepartments()))
                .build();
    }
}
