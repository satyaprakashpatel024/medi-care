package com.care.medi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Schema(hidden = true)
@Entity
@Table(name = "hospital_departments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_hospital_department",
                        columnNames = {"hospital_id", "department_id"}
                ),
                @UniqueConstraint(
                        name = "uk_hospital_dept_head_doctor",
                        columnNames = {"head_doctor_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalDepartment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_hd_hospital"))
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_hd_department"))
    private Department department;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_doctor_id", unique = true,
            foreignKey = @ForeignKey(name = "fk_department_head_doctor"))
    private Doctor headDoctor;

    @Column(nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private boolean active = true;

}