package com.care.medi.dtos.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequestDTO {
    @NotNull
    private Integer userId;
    @NotBlank
    @Size(max = 100)
    private String firstName;
    @NotBlank
    @Size(max = 100)
    private String lastName;
    @Past
    private LocalDate dateOfBirth;
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$")
    private String gender;
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,15}$")
    private String phone;
    @Size(max = 255)
    private String emergencyContact;
    @Pattern(regexp = "^(A|B|AB|O)[+-]$")
    private String bloodType;
}
