package com.care.medi.dtos.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class StaffResponseDTO {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private Long hospitalId;
    private String hospitalName;
    private String emergencyContact;
    private String bloodType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
