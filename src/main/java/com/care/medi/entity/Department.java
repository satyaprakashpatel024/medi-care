package com.care.medi.entity;

import com.care.medi.dtos.request.DepartmentRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Schema(hidden = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "departments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_departments_name", columnNames = "name")
        }
)
public class Department extends BaseEntity {

    @NotBlank(message = "Department name is required")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    // ── Bidirectional mappings ──────────────────────────────────────────────

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HospitalDepartment> hospitalDepartments = new ArrayList<>();

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Doctor> doctors = new ArrayList<>();

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    // ── Helper methods  ──────────────────────────────────────────────
    public static Department toEntity(DepartmentRequestDTO request) {
        return Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
}