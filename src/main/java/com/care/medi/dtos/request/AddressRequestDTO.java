package com.care.medi.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {
    @Pattern(
            regexp = "^(?:(?:\\+|00)91[\\-\\s]?)?[6-9]\\d{9}$",
            message = "Invalid Indian phone number"
    )
    private String phone;
    @NotBlank(message = "Address line 1 is required.")
    @Size(max = 255)
    private String addressLine1;
    @Size(max = 255)
    private String addressLine2;
    @NotBlank(message = "City is required.")
    @Size(max = 100)
    private String city;
    @NotBlank(message = "State is required.")
    @Size(max = 100)
    private String state;
    @NotBlank(message = "Postal code is required.")
    @Size(max = 6, message = "Invalid postal code.")
    private String postalCode;
    @NotBlank
    @Size(max = 100)
    private String country;
    @Size(max = 255)
    @NotBlank(message = "Landmark is required.")
    private String landmark;
    @Pattern(regexp = "^(HOME|WORK)$")
    private String addressType;
    private Boolean isDefault;
}