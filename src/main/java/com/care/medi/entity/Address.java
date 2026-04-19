package com.care.medi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "address", indexes = {
        @Index(name = "idx_address_user_id", columnList = "user_id")
})
@Schema(hidden = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_address_user"))
    private Users user;

    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,15}$", message = "Invalid phone number")
    @Column(name = "phone_number", length = 20)
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
    @Column(nullable = false, length = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String state;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20)
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String country;

    @Size(max = 255)
    @Column
    private String landmark;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", length = 10)
    private AddressType addressType;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

}