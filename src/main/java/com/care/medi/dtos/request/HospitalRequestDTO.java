package com.care.medi.dtos.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class HospitalRequestDTO {
    @NotBlank @Size(max = 255)
    private String name;
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,15}$", message = "Invalid phone number")
    private String phone;
    private HospitalAddressRequestDTO address;
}