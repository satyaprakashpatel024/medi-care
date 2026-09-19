package com.care.medi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "dispense_items", indexes = {
  @Index(name = "idx_di_dispense_record_id", columnList = "dispense_record_id"),
  @Index(name = "idx_di_medication_id", columnList = "medication_id")
})
@Schema(hidden = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLDelete(sql = "UPDATE dispense_items SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class DispenseItem extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dispense_record_id", nullable = false, foreignKey = @ForeignKey(name = "fk_di_dispense_record"))
  @NotNull(message = "Dispense record is required")
  private DispenseRecord dispenseRecord;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "medication_id", nullable = false, foreignKey = @ForeignKey(name = "fk_di_medication"))
  @NotNull(message = "Medication is required")
  private Medication medication;

  @Positive(message = "Quantity must be positive")
  @Column(nullable = false)
  private Integer quantity;

  @PositiveOrZero(message = "Unit price must be positive or zero")
  @Column(name = "unit_price", nullable = false)
  private Double unitPrice;

  @PositiveOrZero(message = "Sub total must be positive or zero")
  @Column(name = "sub_total", nullable = false)
  private Double subTotal;
}
