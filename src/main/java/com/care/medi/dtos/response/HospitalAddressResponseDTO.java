package com.care.medi.dtos.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HospitalAddressResponseDTO {

    private Long id;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String landmark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
