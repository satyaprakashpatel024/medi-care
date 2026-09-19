package com.care.medi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalTime;

@Schema(hidden = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "doctor_schedules",
  indexes = {
    @Index(name = "idx_doc_sched_doctor_id", columnList = "doctor_id"),
    @Index(name = "idx_doc_sched_hospital_id", columnList = "hospital_id")
  },
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_doc_sched_doctor_id", columnNames = "doctor_id")
  }
)
@SQLDelete(sql = "UPDATE doctor_schedules SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class DoctorSchedule extends BaseEntity {

  @NotNull(message = "Doctor ID is required")
  @Column(name = "doctor_id", nullable = false)
  private Long doctorId;

  @NotNull(message = "Hospital ID is required")
  @Column(name = "hospital_id", nullable = false)
  private Long hospitalId;

  @NotNull(message = "Work start time is required")
  @Column(name = "work_start_time", nullable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  @Builder.Default
  private LocalTime workStartTime = LocalTime.of(9, 0);

  @NotNull(message = "Work end time is required")
  @Column(name = "work_end_time", nullable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  @Builder.Default
  private LocalTime workEndTime = LocalTime.of(17, 0);

  @Column(name = "break_start_time")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  @Builder.Default
  private LocalTime breakStartTime = LocalTime.of(13, 0);

  @Column(name = "break_end_time")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  @Builder.Default
  private LocalTime breakEndTime = LocalTime.of(14, 0);

  @NotNull(message = "Slot duration is required")
  @Column(name = "slot_duration_minutes", nullable = false)
  @Builder.Default
  private Integer slotDurationMinutes = 15;

  @Column(name = "working_days", length = 255)
  @Builder.Default
  private String workingDays = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY";

  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private boolean isActive = true;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "doctor_id", foreignKey = @ForeignKey(name = "fk_doc_sched_doctor"), insertable = false, updatable = false)
  private Doctor doctor;
}
