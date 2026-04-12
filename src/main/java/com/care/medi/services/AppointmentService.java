package com.care.medi.services;

import com.care.medi.dtos.request.AppointmentRequestDTO;
import com.care.medi.dtos.request.AppointmentRescheduleDTO;
import com.care.medi.dtos.request.AppointmentUpdateRequestDTO;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface AppointmentService {
    Page<AppointmentListResponseDTO> getAllAppointmentsByHospitalAndDate(Long hospitalId, Integer page, Integer size, String sortBy, LocalDate date);

    AppointmentResponseDTO createAppointment(AppointmentRequestDTO request);

    AppointmentResponseDTO getAppointmentById(Long id);

    @Transactional
    AppointmentResponseDTO rescheduleAppointment(Long id, AppointmentRescheduleDTO request);

    @Transactional
    AppointmentResponseDTO updateAppointment(Long id, AppointmentUpdateRequestDTO request);

    void cancelAppointment(Long id);

    void deleteAppointment(Long id);

    Page<AppointmentResponseDTO> getAppointmentsByHospitalAndPatient(Long hospitalId,Long patientId, int page, int size, String sortBy);

    Page<AppointmentListResponseDTO> getAppointmentsByHospitalAndStatusAndDate(Long hospitalId, String status, int page, int size, String sortBy, LocalDate date);

    Page<AppointmentListResponseDTO> getAppointmentsByDoctorAndDate(Long doctorId, int page, int size, String sortBy, LocalDate filterDate);

    Page<AppointmentResponseDTO> getAppointmentsByPatientAndDate(Long patientId, Long date, int page, int size, String sortBy);
}
