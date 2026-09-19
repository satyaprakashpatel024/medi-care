package com.care.medi.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record AppointmentSlotDTO(
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  LocalTime startTime,

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  LocalTime endTime,

  boolean available,

  String status,

  String formattedTimeRange
) {
}
