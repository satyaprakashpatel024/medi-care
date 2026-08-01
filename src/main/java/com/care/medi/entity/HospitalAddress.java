package com.care.medi.entity;

import com.care.medi.dtos.request.HospitalAddressRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Schema(hidden = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "hospital_address", indexes = {
        @Index(name = "idx_hosp_addr_hospital", columnList = "hospital_id")
})
public class HospitalAddress extends BaseEntity {

    @NotNull(message = "Hospital ID is required.")
    @Column(name = "hospital_id")
    private Long hospitalId;

    // ── Address fields ───────────────────────────────
    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^(?:(?:\\+|00)91[\\-\\s]?)?[6-9]\\d{9}$",
            message = "Invalid phone number, Please provide valid Indian Phone number.")
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @NotBlank(message = "Address line 1 is required")
    @Size(max = 255)
    @Column(name = "address_line1", nullable = false)
    private String addressLine1;

    @Size(max = 255)
    @Column(name = "address_line2")
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100)
    @Column(nullable = false)
    private String state;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20)
    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Size(max = 100)
    @Column(nullable = false)
    private String country;

    @Size(max = 255)
    @Column
    private String landmark;
    // ── Link to Hospital ───────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_hosp_addr_hospital"))
    private Hospital hospital;

    public static HospitalAddress toEntity(Long hospitalId, HospitalAddressRequestDTO request) {
        return HospitalAddress.builder()
                .hospitalId(hospitalId)
                .phoneNumber(request.getPhoneNumber())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .landmark(request.getLandmark())
                .build();
    }

}