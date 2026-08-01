package com.care.medi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(
        name = "medical_records",
        indexes = {
                @Index(name = "idx_mr_patient", columnList = "patient_id"),
                @Index(name = "idx_mr_doctor", columnList = "doctor_id"),
                @Index(name = "idx_mr_appointment", columnList = "appointment_id"),
                @Index(name = "idx_mr_hospital", columnList = "hospital_id"),
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

    @Column(name = "patient_id")
    @NotNull(message = "Patient Id is required.")
    private Long patientId;


    @NotNull(message = "Doctor Id is required.")
    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "appointment_id")
    @NotNull(message = "Appointment ID is required.")
    private Long appointmentId;

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

    /**
     * -------------BIDIRECTIONAL MAPPING------------------------
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_medical_record_appointment"))
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_medical_record_doctor"))
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", updatable = false, insertable = false, foreignKey = @ForeignKey(name = "fk_medical_record_patient"))
    private Patient patient;
}
