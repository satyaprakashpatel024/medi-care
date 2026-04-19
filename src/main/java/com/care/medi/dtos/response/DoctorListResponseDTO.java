package com.care.medi.dtos.response;

import com.care.medi.entity.Doctor;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DoctorListResponseDTO(
        Long id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String phone,
        String speciality,
        Long hospitalId,
        String hospitalName,
        Long departmentId,
        String departmentName,
        String emergencyContact,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt) {
    public static DoctorListResponseDTO toDoctorListResponse(Doctor d) {
        return DoctorListResponseDTO.builder()
                .id(d.getId())
                .firstName(d.getFirstName())
                .lastName(d.getLastName())
                .dateOfBirth(d.getDateOfBirth())
                .gender(d.getGender().toString())
                .phone(d.getPhone())
                .speciality(d.getSpeciality())
                .hospitalId(d.getHospital() != null ? d.getHospital().getId() : null)
                .hospitalName(d.getHospital() != null ? d.getHospital().getName() : null)
                .departmentId(d.getDepartment() != null ? d.getDepartment().getId() : null)
                .departmentName(d.getDepartment() != null ? d.getDepartment().getName() : null)
                .emergencyContact(d.getEmergencyContact())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
