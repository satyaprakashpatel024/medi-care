package com.care.medi.entity;

import com.care.medi.dtos.request.DoctorRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Schema(hidden = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "doctors",
        indexes = {
                @Index(name = "idx_doctors_user_id", columnList = "user_id"),
                @Index(name = "idx_doctors_hospital_id", columnList = "hospital_id"),
                @Index(name = "idx_doctors_department_id", columnList = "department_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_doctors_user_id", columnNames = "user_id")
        }
)
public class Doctor extends BaseEntity {

    @NotNull(message = "Users is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank(message = "First name is required")
    @Size(min = 5, max = 100, message = "First name must be between 5 and 100 characters.")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 5, max = 100, message = "Last name must be between 5 and 100 characters.")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^(?:(?:\\+|00)91[\\-\\s]?)?[6-9]\\d{9}$",
            message = "Invalid phone number, Please provide valid Indian Phone number.")
    @Column(length = 20)
    private String phone;

    @NotBlank(message = "Speciality is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String speciality;

    @Column(name = "hospital_id",nullable = false)
    private Long hospitalId;

    @Column(name = "department_id",nullable = false)
    private Long departmentId;

    @Size(max = 255)
    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "blood_group", length = 7)
    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    @ColumnDefault("true")
    private boolean isActive = true;

    // ── Bidirectional mappings ──────────────────────────────────────────────

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Prescription> prescriptions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id",
            foreignKey = @ForeignKey(name = "fk_doctor_department"),
            insertable = false, // Prevents Hibernate from trying to save this field
            updatable = false   // Prevents Hibernate from trying to update this field
    )
    private Department department;

    // Add the Entity relationship purely to generate the Foreign Key constraint
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "hospital_id",
            foreignKey = @ForeignKey(name = "fk_appointment_hospital"),
            insertable = false, // Prevents Hibernate from trying to save this field
            updatable = false   // Prevents Hibernate from trying to update this field
    )
    private Hospital hospital;

    // ── Helper methods ──────────────────────────────────────────────────────

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        appointment.setDoctor(this);
    }

    public void addPrescription(Prescription prescription) {
        prescriptions.add(prescription);
        prescription.setDoctor(this);
    }

    public static Doctor toEntity(DoctorRequestDTO request,Long userId,Department department,Long hospitalId){
        return Doctor.builder()
                .userId(userId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(Gender.valueOf(request.getGender()))
                .phone(request.getPhone())
                .speciality(request.getSpeciality())
                .hospitalId(hospitalId)
                .department(department)
                .emergencyContact(request.getEmergencyContact())
                .bloodGroup(BloodGroup.valueOf(request.getBloodType()))
                .build();
    }
}