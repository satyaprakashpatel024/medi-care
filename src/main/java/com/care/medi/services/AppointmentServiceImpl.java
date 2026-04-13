package com.care.medi.services;

import com.care.medi.dtos.request.AppointmentRequestDTO;
import com.care.medi.dtos.request.AppointmentRescheduleDTO;
import com.care.medi.dtos.request.AppointmentUpdateRequestDTO;
import com.care.medi.dtos.request.PrescriptionRequestDTO;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import com.care.medi.dtos.response.PatientResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.InvalidRequestException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.exception.ResourceValidationException;
import com.care.medi.repository.*;
import com.care.medi.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@RestControllerAdvice
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DepartmentRepository departmentRepository;
    private final PatientServiceImpl patientService;
    private final HospitalRepository hospitalRepository;

    @Override
    public Page<AppointmentListResponseDTO> getAllAppointmentsByHospitalAndDate(
            Long hospitalId, Integer page, Integer size, String sortBy, LocalDate date) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        // Convert LocalDate to the start and end of that specific day
        // 1. Create the start of the day in IST
        OffsetDateTime startOfDay = date.atStartOfDay()
                .atZone(ZoneId.of(Constants.TIME_ZONE))
                .toOffsetDateTime();

        // 2. Create the end of the day in IST
        OffsetDateTime endOfDay = date.atTime(LocalTime.MAX)
                .atZone(ZoneId.of(Constants.TIME_ZONE))
                .toOffsetDateTime();

        Page<Appointment> all = appointmentRepository.findByHospitalIdAndAppointmentDateBetween(
                hospitalId, startOfDay, endOfDay, pageable);
        return all.map(AppointmentListResponseDTO::fromEntity);
    }

    @Transactional
    @Override
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {
        Map<String, String> errorMap = new HashMap<>();

        // 1. Create Patient
        PatientResponseDTO patient = patientService.createPatient(request.getPatient());

        // 2. Validate Doctor
        Optional<Doctor> doctorOpt = doctorRepository.findById(request.getDoctorId());
        if (doctorOpt.isEmpty()) {
            errorMap.put("doctorId", Constants.DOCTOR_NOT_FOUND + request.getDoctorId());
        }

        // 3. Validate Department
        Optional<Department> departmentOpt = departmentRepository.findById(request.getDepartmentId());
        if (departmentOpt.isEmpty()) {
            errorMap.put("departmentId", Constants.DEPARTMENT_NOT_FOUND + request.getDepartmentId());
        }

        Optional<Hospital> byId = hospitalRepository.findById(request.getHospitalId());
        if (byId.isEmpty()) {
            errorMap.put("hospitalId", Constants.HOSPITAL_NOT_FOUND + request.getHospitalId());
        }

        // 4. If the map has errors, throw a custom exception
        if (!errorMap.isEmpty()) {
            throw new ResourceValidationException(errorMap);
        }

        // 4. Get Patient
        Patient patient1 = patientRepository.findById(patient.id()).get();

        // 5. Proceed with creation if no errors
        Appointment appointment = Appointment.builder()
                .patient(patient1)
                .doctor(doctorOpt.get())
                .department(departmentOpt.get())
                .hospital(byId.get())
                .appointmentDate(request.getAppointmentDate())
                .status(AppointmentStatus.SCHEDULED)
                .createdAt(OffsetDateTime.now())
                .build();

        return AppointmentResponseDTO.fromEntity(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponseDTO getAppointmentById(Long id) {
        Optional<Appointment> byId = appointmentRepository.findById(id);
        if (byId.isEmpty()) {
            throw new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id);
        }
        return AppointmentResponseDTO.fromEntity(byId.get());
    }

    @Transactional
    @Override
    public AppointmentResponseDTO rescheduleAppointment(Long id, AppointmentRescheduleDTO request) {
        Optional<Appointment> byId = appointmentRepository.findById(id);
        if (byId.isEmpty()) {
            throw new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id);
        }
        Appointment appointment = byId.get();
        if (request.getAppointmentDate() != null) {
            appointment.setAppointmentDate(request.getAppointmentDate());
        }
        if (request.getStatus() != null) {
            appointment.setStatus(AppointmentStatus.valueOf(request.getStatus()));
        }
        appointment = appointmentRepository.saveAndFlush(appointment);
        return AppointmentResponseDTO.fromEntity(appointment);
    }

    @Transactional
    @Override
    public AppointmentResponseDTO updateAppointment(Long id, AppointmentUpdateRequestDTO request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id));

        // 1. Handle Prescription (Update existing or Create new)
        PrescriptionRequestDTO pDto = request.getPrescription();
        Prescription prescription = appointment.getPrescription();

        if (prescription == null) {
            // Create new if none exists
            prescription = Prescription.builder()
                    .doctor(appointment.getDoctor())
                    .patient(appointment.getPatient())
                    .appointment(appointment)
                    .createdAt(OffsetDateTime.now())
                    .build();
        }

        // Update prescription fields
        prescription.setMedications(pDto.getMedications());
        prescription.setDosageInstructions(pDto.getDosageInstructions());
        prescription.setNotes(pDto.getNotes());

        // 2. Update Appointment fields
        appointment.setStatus(AppointmentStatus.valueOf(request.getStatus()));
        appointment.setTreatment(request.getTreatment());
        appointment.setNotes(request.getNotes());
        appointment.setPrescription(prescription); // Link it

        // saveAndFlush
        return AppointmentResponseDTO.fromEntity(appointmentRepository.saveAndFlush(appointment));
    }

    @Transactional
    @Override
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id));

        switch (appointment.getStatus()) {
            case SCHEDULED, NO_SHOW -> {
                appointment.setStatus(AppointmentStatus.CANCELLED);
                appointmentRepository.save(appointment); // saveAndFlush is usually overkill here
            }
            default ->
                    throw new InvalidRequestException(Constants.INVALID_APPOINTMENT_STATUS + appointment.getStatus().name());
        }
    }

    @Transactional
    @Override
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id));
        appointment.setStatus(AppointmentStatus.CANCELLED);
    }


    @Override
    public Page<AppointmentResponseDTO> getAppointmentsByHospitalAndPatient(Long hospitalId, Long patientId, int page, int size, String sortBy) {
        boolean b = patientRepository.existsById(patientId);
        if (!b) {
            throw new ResourceNotFoundException(Constants.PATIENT_NOT_FOUND + patientId);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return appointmentRepository
                .findByHospitalAndPatient(hospitalId, patientId, pageable)
                .map(AppointmentResponseDTO::fromEntity);
    }


    @Override
    public Page<AppointmentListResponseDTO> getAppointmentsByHospitalAndStatusAndDate(Long hospitalId, String status, int page, int size, String sortBy, LocalDate date) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        // 1. Create the start of the day in IST
        OffsetDateTime startOfDay = date.atStartOfDay()
                .atZone(ZoneId.of(Constants.TIME_ZONE))
                .toOffsetDateTime();

        // 2. Create the end of the day in IST
        OffsetDateTime endOfDay = date.atTime(LocalTime.MAX)
                .atZone(ZoneId.of(Constants.TIME_ZONE))
                .toOffsetDateTime();
        return appointmentRepository
                .findByHospitalAndStatusAndAppointmentDateBetween(hospitalId, AppointmentStatus.valueOf(status), startOfDay, endOfDay, pageable)
                .map(AppointmentListResponseDTO::fromEntity);
    }

    @Override
    public Page<AppointmentListResponseDTO> getAppointmentsByDoctorAndDate(Long doctorId, int page, int size, String sortBy, LocalDate date) {

        // 1. Create the start of the day in IST
        OffsetDateTime startOfDay = date.atStartOfDay()
                .atZone(ZoneId.of(Constants.TIME_ZONE))
                .toOffsetDateTime();

        // 2. Create the end of the day in IST
        OffsetDateTime endOfDay = date.atTime(LocalTime.MAX)
                .atZone(ZoneId.of(Constants.TIME_ZONE))
                .toOffsetDateTime();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return appointmentRepository
                .findByDoctorIdAndAppointmentDateBetween(doctorId, startOfDay, endOfDay, pageable)
                .map(AppointmentListResponseDTO::fromEntity);
    }

    @Override
    public Page<AppointmentResponseDTO> getAppointmentsByPatientAndDate(Long patientId, Long date, int page, int size, String sortBy) {
        return null;
    }
}
