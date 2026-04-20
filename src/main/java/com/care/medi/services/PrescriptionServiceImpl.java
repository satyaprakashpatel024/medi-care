package com.care.medi.services;

import com.care.medi.dtos.request.PrescriptionRequestDTO;
import com.care.medi.dtos.response.PrescriptionResponseDTO;
import com.care.medi.entity.Appointment;
import com.care.medi.entity.AppointmentStatus;
import com.care.medi.entity.Patient;
import com.care.medi.entity.Prescription;
import com.care.medi.exception.InvalidRequestException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.AppointmentRepository;
import com.care.medi.repository.PatientRepository;
import com.care.medi.repository.PrescriptionRepository;
import com.care.medi.utils.Constants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl {
    private final PrescriptionRepository prescriptionRepository;
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;

    @Transactional(readOnly = true)
    public Page<PrescriptionResponseDTO> getPrescriptionByPatientId(Long hospitalId,Long patientId, int page, int size, String sortBy) {
        if(!patientService.existsByIdAndHospitalId(patientId,hospitalId)){
            throw new ResourceNotFoundException(String.format(Constants.PATIENT_NOT_FOUND_IN_HOSPITAL, patientId));
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return prescriptionRepository.findByPatientId(patientId,pageable);
    }


    public Page<PrescriptionResponseDTO> getPrescriptionByAppointmentId(Long hospitalId, Long appointmentId, int page, int size, String sortBy) {
        if(!appointmentService.existsByIdAndHospitalId(appointmentId,hospitalId)){
            throw new ResourceNotFoundException(String.format(Constants.APPOINTMENT_NOT_FOUND_IN_HOSPITAL, appointmentId));
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Prescription> byAppointmentId = prescriptionRepository.findByAppointmentId(appointmentId,pageable);
        return byAppointmentId.map(PrescriptionResponseDTO::toResponse);
    }

    @Transactional
    public PrescriptionResponseDTO createPrescription(Long hospitalId,PrescriptionRequestDTO request) {
        if(!appointmentService.isAppointmentContextValid(request.getAppointmentId(),hospitalId,request.getDoctorId(),request.getPatientId())){
            throw new InvalidRequestException("You do not have permission to prescribe for this appointment.");
        }
        List<AppointmentStatus> allowedStatuses = List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.NO_SHOW);
        // 1. Fetch only if valid
        Appointment appointment = appointmentRepository.findByIdAndStatusIn(request.getAppointmentId(), allowedStatuses)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found or is already completed/cancelled."));
        Prescription save = prescriptionRepository.save(Prescription.toEntity(appointment, request));
        appointmentService.updateAppointmentStatus(appointment.getId(), AppointmentStatus.COMPLETED);
        return PrescriptionResponseDTO.toResponse(save);
    }
}
