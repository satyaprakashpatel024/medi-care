package com.care.medi.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record DoctorDaySlotsResponseDTO(
  Long doctorId,

  String doctorName,

  Long hospitalId,

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  LocalDate date,

  String dayOfWeek,

  int slotDurationMinutes,

  int totalSlots,

  int availableSlotsCount,

  List<AppointmentSlotDTO> slots
) {
}
