package com.care.medi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appt_patient_id", columnList = "patient_id"),
        @Index(name = "idx_appt_doctor_id", columnList = "doctor_id"),
        @Index(name = "idx_appt_date", columnList = "appointment_date")
})
@Schema(hidden = true)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_appt_patient"))
    @NotNull(message = "Patient is required")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_appt_doctor"))
    @NotNull(message = "Doctor is required")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id",
            foreignKey = @ForeignKey(name = "fk_appt_department"))
    private Department department;

    // One-to-one: each appointment has at most one prescription
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "prescription_id", unique = true,
            foreignKey = @ForeignKey(name = "fk_appt_prescription"))
    private Prescription prescription;

    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(SCHEDULED|CONFIRMED|COMPLETED|CANCELLED|NO_SHOW)$",
            message = "Invalid appointment status")
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Size(max = 500)
    @Column(length = 500)
    private String diagnosis;

    @Size(max = 500)
    @Column(length = 500)
    private String treatment;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
