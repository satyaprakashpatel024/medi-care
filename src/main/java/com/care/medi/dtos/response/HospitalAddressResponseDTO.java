package com.care.medi.dtos.response;

import com.care.medi.entity.HospitalAddress;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record HospitalAddressResponseDTO(
        Long id,
        String phoneNumber,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String country,
        String landmark,
        ZonedDateTime updatedAt
) {
    public static HospitalAddressResponseDTO fromEntity(HospitalAddress address) {
        return HospitalAddressResponseDTO.builder()
                .id(address.getId())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .landmark(address.getLandmark())
                .phoneNumber(address.getPhoneNumber())
                .updatedAt(address.getUpdatedAt())
                .build();
    }

    public static Set<HospitalAddressResponseDTO> fromEntity(Set<HospitalAddress> address) {
        return address.stream()
                .map(HospitalAddressResponseDTO::fromEntity)
                .collect(Collectors.toSet());
    }


}
