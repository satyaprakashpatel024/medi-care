package com.care.medi.dtos.response;


import com.care.medi.entity.HospitalDepartment;
import lombok.Builder;

import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record HospitalDepartmentResponseDTO(
        Long id,
        Long headDoctorId,
        Boolean active,
        String headDoctorName,
        String departmentName
) {

    public static HospitalDepartmentResponseDTO fromEntity(HospitalDepartment hd) {
        return HospitalDepartmentResponseDTO.builder()
                .id(hd.getId())
                .departmentName(hd.getDepartment() != null ? hd.getDepartment().getName() : "Unknown")
                .headDoctorId(hd.getHeadDoctor() != null ? hd.getHeadDoctor().getId() : null)
                .headDoctorName(hd.getHeadDoctor() != null ? STR."\{hd.getHeadDoctor().getFirstName()} \{hd.getHeadDoctor().getLastName()}" : "No Head Assigned")
                .active(hd.isActive())
                .build();
    }



    public static Set<HospitalDepartmentResponseDTO> fromEntity(Set<HospitalDepartment> hospitalDepartments) {
        return hospitalDepartments.stream()
                .map(HospitalDepartmentResponseDTO::fromEntity)
                .collect(Collectors.toSet());
    }
}
