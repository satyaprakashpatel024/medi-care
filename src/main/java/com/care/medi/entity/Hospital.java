package com.care.medi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hospitals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Hospital name is required")
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", foreignKey = @ForeignKey(name = "fk_hospital_address"))
    private Address address;

    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,15}$", message = "Invalid phone number")
    @Column(length = 20)
    private String phone;

    // ── Bidirectional mappings ──────────────────────────────────────────────

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Doctor> doctors = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Staff> staffs = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HospitalDepartment> hospitalDepartments = new ArrayList<>();

    // ── Helper methods ──────────────────────────────────────────────────────

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



