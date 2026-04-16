package com.care.medi.services;

import com.care.medi.dtos.request.AppointmentRequestDTO;
import com.care.medi.dtos.request.AppointmentRescheduleDTO;
import com.care.medi.dtos.request.AppointmentUpdateRequestDTO;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import com.care.medi.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface AppointmentService {
    Page<AppointmentListResponseDTO> getAllAppointmentsByHospitalAndDate(Long hospitalId, Integer page, Integer size, String sortBy, LocalDate date);

    AppointmentResponseDTO createAppointment(Long hospitalId, AppointmentRequestDTO request);

    AppointmentResponseDTO getAppointmentByIdAndHospital(Long id, Long hospitalId);

    @Transactional
    AppointmentResponseDTO rescheduleAppointment(Long id, AppointmentRescheduleDTO request, Long hospitalId);


    @Transactional
    AppointmentResponseDTO updateAppointment(Long id, Long hospitalId, AppointmentUpdateRequestDTO request);

    void cancelAppointment(Long id, Long hospitalId);

    void deleteAppointment(Long id, Long hospitalId);

    Page<AppointmentResponseDTO> getAppointmentsByHospitalAndPatient(Long hospitalId, Long patientId, int page, int size, String sortBy);

    Page<AppointmentListResponseDTO> getAppointmentsByHospitalAndStatusAndDate(Long hospitalId, AppointmentStatus status, int page, int size, String sortBy, LocalDate date);

    Page<AppointmentListResponseDTO> getAppointmentsByDoctorAndDate(Long doctorId, int page, int size, String sortBy, LocalDate filterDate);

    Page<AppointmentResponseDTO> getAppointmentsByPatientAndDate(Long patientId, LocalDate date, int page, int size, String sortBy);
}
