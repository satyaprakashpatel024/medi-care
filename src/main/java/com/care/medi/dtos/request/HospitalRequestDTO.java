package com.care.medi.dtos.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class HospitalRequestDTO {
    @NotBlank @Size(max = 255)
    private String name;
    @Pattern(regexp = "^(?:(?:\\+|00)91[\\-\\s]?)?[6-9]\\d{9}$",
            message = "Invalid phone number, Please provide valid Indian Phone number.")
    private String phone;
    @NotNull(message = "Address is required")
    private HospitalAddressRequestDTO address;
}