package com.care.medi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "medication")
@Schema(hidden = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLDelete(sql = "UPDATE medication SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Medication extends BaseEntity {

    @NotBlank(message = "Medication name is required")
    @Column(nullable = false)
    private String name;

    @Column(length = 255)
    private String manufacturer;

    @Column(name = "dosage_form", length = 50)
    private String dosageForm;

    @PositiveOrZero(message = "Unit price must be positive or zero")
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @PositiveOrZero(message = "Stock quantity must be positive or zero")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @PositiveOrZero(message = "Reorder level must be positive or zero")
    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;
}
