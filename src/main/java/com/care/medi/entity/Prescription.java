package com.care.medi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Schema(hidden = true)
@Entity
@Table(name = "prescription", indexes = {
        @Index(name = "idx_prescription_patient_id", columnList = "patient_id"),
        @Index(name = "idx_prescription_doctor_id", columnList = "doctor_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_prescription_patient"))
    @NotNull(message = "Patient is required")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_prescription_doctor"))
    @NotNull(message = "Doctor is required")
    private Doctor doctor;

    @NotBlank(message = "Medications are required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String medications;

    @Column(name = "dosage_instructions", columnDefinition = "TEXT")
    private String dosageInstructions;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── Bidirectional mapping ───────────────────────────────────────────────

    @OneToOne(mappedBy = "prescription")
    private Appointment appointment;
}