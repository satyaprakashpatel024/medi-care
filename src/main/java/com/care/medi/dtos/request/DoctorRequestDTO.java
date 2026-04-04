package com.care.medi.dtos.request;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DoctorRequestDTO {
    @NotNull
    private Long userId;
    @NotBlank @Size(max = 100)
    private String firstName;
    @NotBlank @Size(max = 100)
    private String lastName;
    @Past
    private LocalDate dateOfBirth;
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$")
    private String gender;
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,15}$")
    private String phone;
    @NotBlank @Size(max = 150)
    private String speciality;
    @NotNull
    private Long hospitalId;
    @NotNull
    private Long departmentId;
    @Size(max = 255)
    private String emergencyContact;
    @Pattern(regexp = "^(A|B|AB|O)[+-]$")
    private String bloodType;
}
