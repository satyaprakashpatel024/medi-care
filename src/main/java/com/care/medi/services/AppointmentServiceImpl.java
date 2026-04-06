package com.care.medi.services;

import com.care.medi.dtos.request.AppointmentRequestDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import com.care.medi.entity.Appointment;
import com.care.medi.entity.AppointmentStatus;
import com.care.medi.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService{

    private  final AppointmentRepository appointmentRepository;
    @Override
    public Page<AppointmentResponseDTO> getAllAppointments(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Appointment> all = appointmentRepository.findAll(pageable);
        return all.map(AppointmentResponseDTO::fromEntity);
    }

    @Override
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {
        return null;
    }

    @Override
    public AppointmentResponseDTO getAppointmentById(Long id) {
        return null;
    }

    @Override
    public AppointmentResponseDTO updateAppointment(Long id, AppointmentRequestDTO request) {
        return null;
    }

    @Override
    public void deleteAppointment(Long id) {

    }

    @Override
    public Page<AppointmentResponseDTO> getAppointmentsByDoctor(Long doctorId, int page, int size, String sortBy) {
        return null;
    }

    @Override
    public Page<AppointmentResponseDTO> getAppointmentsByPatient(Long patientId, int page, int size, String sortBy) {
        return null;
    }

    @Override
    public Page<AppointmentResponseDTO> getAppointmentsByDate(Long date, int page, int size, String sortBy) {
        return null;
    }

    @Override
    public Page<AppointmentResponseDTO> getAppointmentsByStatus(String status, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return appointmentRepository
                .findByStatus(AppointmentStatus.valueOf(status), pageable)
                .map(AppointmentResponseDTO::fromEntity);
    }

    @Override
    public Page<AppointmentResponseDTO> getAppointmentsByDoctorAndDate(Long doctorId, Long date, int page, int size, String sortBy) {
        return null;
    }

    @Override
    public Page<AppointmentResponseDTO> getAppointmentsByPatientAndDate(Long patientId, Long date, int page, int size, String sortBy) {
        return null;
    }
}
