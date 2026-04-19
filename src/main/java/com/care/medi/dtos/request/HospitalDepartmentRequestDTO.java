package com.care.medi.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class HospitalDepartmentRequestDTO {
    @NotNull(message = "Head Doctor ID is required.")
    Long headDoctorId;

    @NotNull(message = "Active is required.")
    Boolean active = true;

    @NotNull(message = "Department ID is required.")
    Long departmentId;

}
