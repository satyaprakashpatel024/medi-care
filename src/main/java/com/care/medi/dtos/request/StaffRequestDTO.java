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
public class StaffRequestDTO {
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;
    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past.")
    private LocalDate dateOfBirth;
    @NotNull(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$")
    private String gender;
    @Pattern(regexp = "^(?:(?:\\+|00)91[\\-\\s]?)?[6-9]\\d{9}$",
            message = "Invalid phone number, Please provide valid Indian Phone number.")
    private String phone;
    @NotNull(message = "Hospital ID is required")
    @Positive(message = "Hospital ID must be a positive number.")
    private Integer hospitalId;
    @Size(max = 255)
    private String emergencyContact;
    @Pattern(regexp = "^(A_POS|B_POS|AB_POS|O_POS|A_NEG|B_NEG|AB_NEG|O_NEG)$")
    private String bloodGroup;
}
