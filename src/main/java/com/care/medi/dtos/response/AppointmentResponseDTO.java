package com.care.medi.dtos.response;

import com.care.medi.entity.Appointment;
import com.care.medi.utils.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.apache.tomcat.util.bcel.Const;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AppointmentResponseDTO(
        Long appointmentId,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        Long departmentId,
        String departmentName,
        String appointmentDate,
        String appointmentTime,
        String status,
        String treatment,
        String notes,
        String updatedAt) {


    public static AppointmentResponseDTO fromEntity(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .appointmentId(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(STR."\{appointment.getPatient().getFirstName()} \{appointment.getPatient().getLastName()}")
                .doctorId(appointment.getDoctor().getId())
                .doctorName(STR."\{appointment.getDoctor().getFirstName()} \{appointment.getDoctor().getLastName()}")
                .departmentId(appointment.getDepartment().getId())
                .departmentName(appointment.getDepartment().getName())
                .appointmentDate(appointment.getAppointmentDate().format(Constants.HUMAN_DATE_FORMAT))
                .appointmentTime(appointment.getStartTime().format(Constants.HUMAN_TIME_FORMAT))
                .status(appointment.getStatus().name())
                .treatment(appointment.getTreatment())
                .notes(appointment.getNotes())
                .updatedAt(appointment.getUpdatedAt().format(Constants.HUMAN_DATETIME_FORMAT))
                .build();
    }

    public static Set<AppointmentResponseDTO> fromEntity(Set<Appointment> appointment) {
        return appointment.stream().map(AppointmentResponseDTO::fromEntity).collect(Collectors.toSet());
    }
}