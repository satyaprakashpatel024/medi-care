package com.care.medi.dtos.request;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DoctorUpdateRequestDTO {
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
    @Size(max = 150)
    private String speciality;
    private Long hospitalId;
    private Long departmentId;
    @Size(max = 255) 
    private String emergencyContact;
    @Pattern(regexp = "^(A_POS|B_POS|AB_POS|O_POS|A_NEG|B_NEG|AB_NEG|O_NEG)$")
    private String bloodType;
}
