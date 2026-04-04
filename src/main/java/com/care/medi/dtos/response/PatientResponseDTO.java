package com.care.medi.dtos.response;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PatientResponseDTO {
    private Long id;
    private Long userId;
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
