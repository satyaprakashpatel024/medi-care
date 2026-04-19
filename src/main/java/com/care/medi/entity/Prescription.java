package com.care.medi.entity;

import com.care.medi.dtos.request.PrescriptionRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@Schema(hidden = true)
@Entity
@Table(name = "prescription", indexes = {
        @Index(name = "idx_prescription_patient_id", columnList = "patient_id"),
        @Index(name = "idx_prescription_doctor_id", columnList = "doctor_id"),
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Prescription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_prescription_patient"))
    @NotNull(message = "Patient is required")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_prescription_doctor"))
    @NotNull(message = "Doctor is required")
    private Doctor doctor;

    @NotBlank(message = "Medications are required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String medications;

    @Column(name = "dosage_instructions", columnDefinition = "TEXT")
    private String dosageInstructions;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id",nullable = true, foreignKey = @ForeignKey(name = "fk_prescription_appointment"))
    private Appointment appointment;


    // ── Bidirectional mapping ───────────────────────────────────────────────



    // ── Helper methods ───────────────────────────────────────────────────────
    public static Prescription toEntity(Appointment appointment, PrescriptionRequestDTO pDto) {
        return Prescription.builder()
                .doctor(appointment.getDoctor())
                .patient(appointment.getPatient())
                .appointment(appointment)
                .createdAt(ZonedDateTime.now())
                .medications(pDto.getMedications())
                .dosageInstructions(pDto.getDosageInstructions())
                .notes(pDto.getNotes())
                .build();
    }

}