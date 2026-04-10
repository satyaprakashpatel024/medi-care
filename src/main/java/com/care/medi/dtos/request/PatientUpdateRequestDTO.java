package com.care.medi.dtos.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientUpdateRequestDTO {
    @Size(min = 5, max = 100,message = "First name must be between 5 and 100 characters.")
    private String firstName;
    @Size(min = 5, max = 100,message = "Last name must be between 5 and 100 characters.")
    private String lastName;
    @Past(message = "Date of birth must be in the past.")
    private LocalDate dateOfBirth;
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$")
    private String gender;
    @Pattern(regexp = "^(?:(?:\\+|00)91[\\-\\s]?)?[6-9]\\d{9}$",
            message = "Invalid phone number, Please provide valid Indian Phone number.")
    private String phone;
    @Size(max = 255)
    private String emergencyContact;
    @Pattern(regexp = "^(A_POS|B_POS|AB_POS|O_POS|A_NEG|B_NEG|AB_NEG|O_NEG)$")
    private String bloodType;
}