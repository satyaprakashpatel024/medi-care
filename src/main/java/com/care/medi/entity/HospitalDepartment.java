package com.care.medi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hospital_departments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_hospital_department",
                columnNames = {"hospital_id", "department_id"}
        )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HospitalDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_hd_hospital"))
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_hd_department"))
    private Department department;
}