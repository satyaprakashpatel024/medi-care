package com.care.medi.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AppointmentAnalyticsResponseDTO(
        Long hospitalId,
        long totalAppointments,
        long scheduledCount,
        long confirmedCount,
        long completedCount,
        long cancelledCount
) {
}
