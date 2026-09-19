package com.care.medi.dtos.response;

import com.care.medi.entity.DoctorSchedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Builder
public record DoctorScheduleResponseDTO(
  Long id,
  Long doctorId,
  Long hospitalId,
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  LocalTime workStartTime,
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  LocalTime workEndTime,
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  LocalTime breakStartTime,
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  LocalTime breakEndTime,
  int slotDurationMinutes,
  List<String> workingDays,
  boolean isActive
) {
  public static DoctorScheduleResponseDTO fromEntity(DoctorSchedule schedule) {
    if (schedule == null) return null;
    List<String> days = schedule.getWorkingDays() != null && !schedule.getWorkingDays().isBlank()
      ? Arrays.asList(schedule.getWorkingDays().split(","))
      : List.of();
    return DoctorScheduleResponseDTO.builder()
      .id(schedule.getId())
      .doctorId(schedule.getDoctorId())
      .hospitalId(schedule.getHospitalId())
      .workStartTime(schedule.getWorkStartTime())
      .workEndTime(schedule.getWorkEndTime())
      .breakStartTime(schedule.getBreakStartTime())
      .breakEndTime(schedule.getBreakEndTime())
      .slotDurationMinutes(schedule.getSlotDurationMinutes() != null ? schedule.getSlotDurationMinutes() : 15)
      .workingDays(days)
      .isActive(schedule.isActive())
      .build();
  }
}
