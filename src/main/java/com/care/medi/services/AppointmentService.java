package com.care.medi.services;

import com.care.medi.dtos.request.AppointmentRequestDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;

import org.springframework.data.domain.Page;

public interface AppointmentService {
    Page<AppointmentResponseDTO> getAllAppointments(int page, int size, String sortBy);

    AppointmentResponseDTO createAppointment(AppointmentRequestDTO request);

    AppointmentResponseDTO getAppointmentById(Long id);

    AppointmentResponseDTO updateAppointment(Long id, AppointmentRequestDTO request);

    void deleteAppointment(Long id);

    Page<AppointmentResponseDTO> getAppointmentsByDoctor(Long doctorId, int page, int size, String sortBy);

    Page<AppointmentResponseDTO> getAppointmentsByPatient(Long patientId, int page, int size, String sortBy);

    Page<AppointmentResponseDTO> getAppointmentsByDate(Long date, int page, int size, String sortBy);

    Page<AppointmentResponseDTO> getAppointmentsByStatus(String status, int page, int size, String sortBy);

    Page<AppointmentResponseDTO> getAppointmentsByDoctorAndDate(Long doctorId, Long date, int page, int size, String sortBy);

    Page<AppointmentResponseDTO> getAppointmentsByPatientAndDate(Long patientId, Long date, int page, int size, String sortBy);
}
