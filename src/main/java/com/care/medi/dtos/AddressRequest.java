package com.care.medi.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AddressRequest {
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,15}$")
    private String phoneNumber;
    @NotBlank @Size(max = 255)
    private String addressLine1;
    @Size(max = 255)
    private String addressLine2;
    @NotBlank @Size(max = 100)
    private String city;
    @NotBlank @Size(max = 100)
    private String state;
    @NotBlank @Size(max = 20)
    private String postalCode;
    @NotBlank @Size(max = 100)
    private String country;
    @Size(max = 255)
    private String landmark;
    @Pattern(regexp = "^(HOME|WORK|BILLING|SHIPPING)$")
    private String addressType;
    private Boolean isDefault;
}