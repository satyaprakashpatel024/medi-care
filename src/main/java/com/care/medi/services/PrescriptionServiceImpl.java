package com.care.medi.services;

import com.care.medi.dtos.request.PrescriptionRequestDTO;
import com.care.medi.dtos.response.PrescriptionResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.InvalidRequestException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.AppointmentRepository;
import com.care.medi.repository.MedicationRepository;
import com.care.medi.repository.PrescriptionRepository;
import com.care.medi.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl {
  private final PrescriptionRepository prescriptionRepository;
  private final PatientService patientService;
  private final AppointmentService appointmentService;
  private final AppointmentRepository appointmentRepository;
  private final MedicationRepository medicationRepository;

  @Transactional(readOnly = true)
  public Page<PrescriptionResponseDTO> getPrescriptionByPatientId(Long hospitalId, Long patientId, int page, int size, String sortBy) {
    if (!patientService.existsByIdAndHospitalId(patientId, hospitalId)) {
      throw new ResourceNotFoundException(String.format(Constants.PATIENT_NOT_FOUND_IN_HOSPITAL, patientId));
    }
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    Page<Prescription> byPatientId = prescriptionRepository.findByPatientId(patientId, pageable);
    return byPatientId.map(PrescriptionResponseDTO::fromEntity);
  }

  public Page<PrescriptionResponseDTO> getPrescriptionByAppointmentId(Long hospitalId, Long appointmentId, int page, int size, String sortBy) {
    if (!appointmentService.existsByIdAndHospitalId(appointmentId, hospitalId)) {
      throw new ResourceNotFoundException(String.format(Constants.APPOINTMENT_NOT_FOUND_IN_HOSPITAL, appointmentId));
    }
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    Page<Prescription> byAppointmentId = prescriptionRepository.findByAppointmentId(appointmentId, pageable);
    return byAppointmentId.map(PrescriptionResponseDTO::fromEntity);
  }

  @Transactional
  public PrescriptionResponseDTO assignPrescriptionToAppointment(Long hospitalId, PrescriptionRequestDTO request) {
    if (!appointmentService.isAppointmentContextValid(request.getAppointmentId(), hospitalId, request.getDoctorId(), request.getPatientId())) {
      throw new InvalidRequestException("You do not have permission to prescribe for this appointment.");
    }
    List<AppointmentStatus> allowedStatuses = List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.NO_SHOW);
    // 1. Fetch only if valid
    Appointment appointment = appointmentRepository.findByIdAndStatusIn(request.getAppointmentId(), allowedStatuses)
      .orElseThrow(() -> new ResourceNotFoundException(
        "Appointment not found or is already completed/cancelled."));
    Prescription prescription = Prescription.toEntity(appointment, request);
    if (request.getItems() != null && !request.getItems().isEmpty()) {
      for (var itemReq : request.getItems()) {
        Medication medication = medicationRepository.findById(itemReq.getMedicationId())
          .orElseThrow(() -> new ResourceNotFoundException("Medication not found: " + itemReq.getMedicationId()));
        PrescriptionItem item = PrescriptionItem.builder()
          .prescription(prescription)
          .medication(medication)
          .dosageInstructions(itemReq.getDosageInstructions())
          .notes(itemReq.getNotes())
          .build();
        prescription.getItems().add(item);
      }
    }
    Prescription save = prescriptionRepository.save(prescription);
    appointmentService.updateAppointmentStatus(appointment.getId(), AppointmentStatus.COMPLETED);
    return PrescriptionResponseDTO.toResponse(save);
  }
}
