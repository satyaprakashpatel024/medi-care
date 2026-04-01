package com.care.medi.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class HospitalRequest {
    @NotBlank @Size(max = 255)
    private String name;
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,15}$")
    private String phone;
    private AddressRequest address;
}