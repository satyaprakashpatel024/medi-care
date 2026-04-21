package com.care.medi.entity;

import com.care.medi.utils.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Schema(hidden = true)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "appointments",
        indexes = {
                @Index(name = "idx_appt_patient_id", columnList = "patient_id"),
                @Index(name = "idx_appt_doctor_id", columnList = "doctor_id"),
                @Index(name = "idx_appt_date", columnList = "appointment_date")
        }
)
public class Appointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appointment_patient"))
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appointment_doctor"))
    private Doctor doctor;

    @NotNull(message = "Hospital is required")
    @Column(name = "hospital_id")
    private Long hospitalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_appointment_department"))
    private Department department;

    @NotNull(message = "Appointment date is required")
    @Column(name = "appointment_date", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Kolkata")
    private ZonedDateTime appointmentDate;

    @NotNull(message = "Status is required")
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Size(max = 500)
    @Column(length = 500)
    private String treatment;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // ── Bidirectional mappings ──────────────────────────────────────────────

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "appointment", cascade = CascadeType.MERGE)
    @Builder.Default
    private List<Prescription> prescription = new ArrayList<>();

    // Add the Entity relationship purely to generate the Foreign Key constraint
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "hospital_id",
            foreignKey = @ForeignKey(name = "fk_appointment_hospital"),
            insertable = false, // Prevents Hibernate from trying to save this field
            updatable = false   // Prevents Hibernate from trying to update this field
    )
    private Hospital hospital;

    public static Appointment toEntity(Patient patientEntity, Doctor doctor, Department department, Long hospitalId, ZonedDateTime rawTime){
        return Appointment.builder()
                .patient(patientEntity)
                .doctor(doctor)
                .department(department)
                .hospitalId(hospitalId)
                .appointmentDate(rawTime)
                .status(AppointmentStatus.SCHEDULED)
                .createdAt(ZonedDateTime.now(Constants.ZONE_ID))
                .build();
    }
}
