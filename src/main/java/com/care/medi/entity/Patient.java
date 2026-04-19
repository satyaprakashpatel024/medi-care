package com.care.medi.entity;

import com.care.medi.dtos.request.PatientRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Schema(hidden = true)
@Entity
@Table(name = "patients",
        indexes = {@Index(name = "idx_patients_user_id", columnList = "user_id")},
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_patients_user_id", columnNames = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_patient_user"))
    private Users user;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 2)
    private Set<Insurance> insurances = new HashSet<>();

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Pattern(regexp = "^(?:(?:\\+|00)91[\\-\\s]?)?[6-9]\\d{9}$",
            message = "Invalid phone number, Please provide valid Indian Phone number.")
    @Column(length = 20)
    private String phone;

    @Size(max = 255)
    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "blood_group", length = 7)
    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    @NotNull(message = "Hospital ID is required")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "hospital_id", nullable = false, foreignKey = @ForeignKey(name = "fk_patient_hospital"))
    private Hospital hospital;

    // ── Bidirectional mappings ──────────────────────────────────────────────

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 3)
    private Set<Appointment> appointments = new HashSet<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Prescription> prescriptions = new HashSet<>();

    // ── Helper methods ──────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(this.getId()+" "+this.hospital.getId());
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        appointment.setPatient(this);
    }

    public void addPrescription(Prescription prescription) {
        prescriptions.add(prescription);
        prescription.setPatient(this);
    }

    public static Patient toEntity(PatientRequestDTO patient,Users user){
        return Patient.builder()
                .user(user)
                .phone(patient.getPhone())
                .emergencyContact(patient.getEmergencyContact())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(Gender.valueOf(patient.getGender().toUpperCase()))
                .bloodGroup(BloodGroup.valueOf(patient.getBloodGroup().toUpperCase()))
                .build();
    }

}