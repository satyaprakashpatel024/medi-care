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
    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    @Size(max = 255, message = "Email must be at most 255 characters long")
    private String email;
    @NotBlank(message = "First name is required.")
    @Size(max = 100)
    private String firstName;
    @NotBlank(message = "Last name is required.")
    @Size(max = 100)
    private String lastName;
    @Past(message = "Date of birth must be in the past.")
    @NotNull(message = "Date of birth is required.")
    private LocalDate dateOfBirth;
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$")
    private String gender;
    @Pattern(regexp = "^(?:(?:\\+|00)91[\\-\\s]?)?[6-9]\\d{9}$",
            message = "Invalid phone number, Please provide valid Indian Phone number.")
    private String phone;
    @Size(max = 255)
    private String emergencyContact;
    @Pattern(regexp = "^(A_POS|B_POS|AB_POS|O_POS|A_NEG|B_NEG|AB_NEG|O_NEG)$")
    private String bloodGroup;
}
