package com.care.medi.dtos;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PatientResponse {
    private Integer id;
    private Integer userId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String emergencyContact;
    private String bloodType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
