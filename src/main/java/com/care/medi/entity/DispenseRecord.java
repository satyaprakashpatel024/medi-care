package com.care.medi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dispense_records", indexes = {
        @Index(name = "idx_dispense_patient_id", columnList = "patient_id"),
        @Index(name = "idx_dispense_prescription_id", columnList = "prescription_id")
})
@Schema(hidden = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLDelete(sql = "UPDATE dispense_records SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class DispenseRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_dispense_patient"))
    @NotNull(message = "Patient is required")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = true, foreignKey = @ForeignKey(name = "fk_dispense_prescription"))
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensed_by_id", nullable = false, foreignKey = @ForeignKey(name = "fk_dispense_staff"))
    @NotNull(message = "Dispenser (Staff) is required")
    private Staff dispensedBy;

    @PositiveOrZero(message = "Total amount must be positive or zero")
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @NotNull(message = "Dispense status is required")
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private DispenseStatus dispenseStatus;

    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20, nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "dispense_date", nullable = false)
    private ZonedDateTime dispenseDate;

    @OneToMany(mappedBy = "dispenseRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    @lombok.Builder.Default
    private List<DispenseItem> items = new ArrayList<>();
}
