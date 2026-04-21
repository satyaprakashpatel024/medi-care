package com.care.medi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(
        name = "medical_records",
        indexes = {
                @Index(name = "idx_mr_patient",     columnList = "patient_id"),
                @Index(name = "idx_mr_doctor",      columnList = "doctor_id"),
                @Index(name = "idx_mr_appointment", columnList = "appointment_id"),
                @Index(name = "idx_mr_hospital",    columnList = "hospital_id"),
                @Index(name = "idx_mr_record_date", columnList = "record_date")
        }
)
@Schema(hidden = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MedicalRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false,foreignKey = @ForeignKey(name = "fk_medical_record_patient"))
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false,foreignKey = @ForeignKey(name = "fk_medical_record_doctor"))
    private Doctor doctor;

    /**
     * Optional link to the appointment that generated this record.
     * Nullable — records can also be created retrospectively
     * (e.g. importing historical data).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id",foreignKey = @ForeignKey(name = "fk_medical_record_appointment"))
    private Appointment appointment;

    /**
     * Denormalized hospital ID for fast multi-tenant filtering
     * without joining through patient or doctor.
     */
    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(nullable = false, length = 1000)
    private String diagnosis;

    @Column(length = 1000)
    private String symptoms;

    @Column(name = "treatment_plan", length = 2000)
    private String treatmentPlan;

    @Column(name = "follow_up_notes", length = 1000)
    private String followUpNotes;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    /**
     * ACTIVE   – the current, active record for this condition
     * ARCHIVED – superseded by a newer record (never hard-deleted)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RecordStatus status = RecordStatus.ACTIVE;

}
