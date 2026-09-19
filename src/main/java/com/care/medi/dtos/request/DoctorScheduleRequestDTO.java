package com.care.medi.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleRequestDTO {

  @NotNull(message = "Work start time is required")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  private LocalTime workStartTime;

  @NotNull(message = "Work end time is required")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  private LocalTime workEndTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  private LocalTime breakStartTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  private LocalTime breakEndTime;

  @Min(value = 5, message = "Slot duration must be at least 5 minutes")
  @Max(value = 120, message = "Slot duration must not exceed 120 minutes")
  @Builder.Default
  private Integer slotDurationMinutes = 15;

  private List<String> workingDays;
}
