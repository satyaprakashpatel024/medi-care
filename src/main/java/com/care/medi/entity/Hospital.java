package com.care.medi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Schema(hidden = true)
@Entity
@Table(name = "hospitals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital extends BaseEntity {

    @NotBlank(message = "Hospital name is required")
    @Size(min = 10, max = 255, message = "Hospital name must be between 10 and 255 characters")
    @Column(nullable = false)
    private String name;

    @Pattern(regexp = "^(?:(?:\\+|00)91[\\-\\s]?)?[6-9]\\d{9}$",
            message = "Invalid phone number, Please provide valid Indian Phone number.")
    @Column(length = 15)
    private String phone;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 3)
    private Set<HospitalAddress> addresses = new HashSet<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<HospitalDepartment> hospitalDepartments = new HashSet<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Staff> staffs = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Doctor> doctors = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Patient> patients = new ArrayList<>();

    public void addAddress(HospitalAddress address) {
        addresses.add(address);
        address.setHospital(this);
    }

    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
        doctor.setHospital(this);
    }

    public void addStaff(Staff staff) {
        staffs.add(staff);
        staff.setHospital(this);
    }

    public void addDepartment(Department department) {
        HospitalDepartment hd = HospitalDepartment.builder()
                .hospital(this)
                .department(department)
                .build();
        hospitalDepartments.add(hd);
        department.getHospitalDepartments().add(hd);
    }
}