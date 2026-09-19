package com.care.medi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Schema(hidden = true)
@Entity
@Table(name = "prescription_items", indexes = {
  @Index(name = "idx_pi_prescription_id", columnList = "prescription_id"),
  @Index(name = "idx_pi_medication_id", columnList = "medication_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLDelete(sql = "UPDATE prescription_items SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class PrescriptionItem extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "prescription_id", nullable = false, foreignKey = @ForeignKey(name = "fk_prescription_item_prescription"))
  private Prescription prescription;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "medication_id", nullable = false, foreignKey = @ForeignKey(name = "fk_prescription_item_medication"))
  @NotNull(message = "Medication is required")
  private Medication medication;

  @Column(name = "dosage_instructions", columnDefinition = "TEXT")
  private String dosageInstructions;

  @Column(columnDefinition = "TEXT")
  private String notes;
}
