package com.care.medi.entity;

import com.care.medi.utils.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_appt_doctor_date_time", columnNames = {"doctor_id", "appointment_date", "start_time", "end_time"})
        }
)
@SQLDelete(sql = "UPDATE appointments SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Appointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appointment_patient"))
    private Patient patient;

    @NotNull(message = "Doctor is required")
    @Column(name = "doctor_id")
    private Long doctorId;

    @NotNull(message = "Hospital is required")
    @Column(name = "hospital_id")
    private Long hospitalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_appointment_department"))
    private Department department;

    @NotNull(message = "Appointment date is required")
    @Column(name = "appointment_date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;

    @Column(name = "start_time")
    @NotNull(message = "Start time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss a")
    private LocalTime startTime;

    @Column(name = "end_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss a")
    @NotNull(message = "End time is required")
    private LocalTime endTime;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appointment", cascade = CascadeType.MERGE)
    @Builder.Default
    private List<Prescription> prescription = new ArrayList<>();

    // Add the Entity relationship purely to generate the Foreign Key constraint
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", foreignKey = @ForeignKey(name = "fk_appointment_hospital"), insertable = false, updatable = false)
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", foreignKey = @ForeignKey(name = "fk_appointment_doctor"), insertable = false, updatable = false)
    private Doctor doctor;

    public static Appointment toEntity(Patient patientEntity, Doctor doctor, Department department, Long hospitalId, LocalDate date, LocalTime startTime) {
        return Appointment.builder()
                .patient(patientEntity)
                .doctorId(doctor.getId())
                .department(department)
                .hospitalId(hospitalId)
                .appointmentDate(date)
                .status(AppointmentStatus.SCHEDULED)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(10))
                .createdAt(ZonedDateTime.now(Constants.ZONE_ID))
                .build();
    }

    public LocalTime setEndTime() {
        if (this.startTime != null)
            this.endTime = startTime.plusMinutes(10);
        return this.endTime;
    }
}
