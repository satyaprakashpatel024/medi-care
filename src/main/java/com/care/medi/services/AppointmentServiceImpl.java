package com.care.medi.services;

import com.care.medi.dtos.request.*;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import com.care.medi.dtos.response.PatientResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.InvalidRequestException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.exception.ResourceValidationException;
import com.care.medi.repository.*;
import com.care.medi.utils.Constants;
import com.care.medi.utils.Helpers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
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
    private final PrescriptionRepository prescriptionRepository;

    @Override
    public Page<AppointmentListResponseDTO> getAllAppointmentsByHospitalAndDate(
            Long hospitalId, Integer page, Integer size, String sortBy, LocalDate date) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        // Convert LocalDate to the start and end of that specific day
        // 1. Create the start of the day in IST
        ZonedDateTime startOfDay = Helpers.getStartOfTheDay(date);

        // 2. Create the end of the day in IST
        ZonedDateTime endOfDay = Helpers.getEndOfTheDay(date);

        Page<Appointment> all = appointmentRepository.findByHospitalIdAndAppointmentDateBetween(
                hospitalId, startOfDay, endOfDay, pageable);
        return all.map(AppointmentListResponseDTO::fromEntity);
    }

    @Override
    // Appointment slots are every 10 minutes: 09:00, 09:10, 09:20 ...
    @Transactional
    public AppointmentResponseDTO createAppointment(Long hospitalId, AppointmentRequestDTO request) {
        Map<String, String> errorMap = new HashMap<>();

        // 1. Resolve Patient (Existing or New)
        Patient patientEntity = resolvePatient(hospitalId, request.getPatient(), errorMap);

        // 2. Validate Domain Entities (Doctor, Department, Hospital)
        Doctor doctor = validateDoctor(hospitalId, request.getDoctorId(), errorMap);
        Department department = validateDepartment(request.getDepartmentId(), errorMap);
        Hospital hospital = validateHospital(hospitalId, errorMap);

        // 3. Parse and Normalize Date
        ZonedDateTime rawTime = Helpers.parseAndRoundToNearestTenMinutes(request.getAppointmentDate(), errorMap);

        // 4. Guard Clause: Throw if any errors collected
        if (!errorMap.isEmpty() || patientEntity == null) {
            throw new ResourceValidationException(errorMap);
        }

        // 5. Build and persist
        Appointment appointment =Appointment.toEntity(patientEntity, doctor, department, hospital, rawTime);

        return AppointmentResponseDTO.fromEntity(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponseDTO getAppointmentByIdAndHospital(Long id, Long hospitalId) {
        Optional<Appointment> byId = appointmentRepository.findByIdAndHospitalId(id, hospitalId);
        if (byId.isPresent()) {
            return AppointmentResponseDTO.fromEntity(byId.get());
        }
        throw new ResourceNotFoundException(STR."\{Constants.APPOINTMENT_NOT_FOUND}\{id} And Hospital Id : \{hospitalId}");
    }

    @Transactional
    @Override
    public AppointmentResponseDTO rescheduleAppointment(Long id, AppointmentRescheduleDTO request, Long hospitalId) {
        Appointment appointment = appointmentRepository.findByIdAndHospitalId(id, hospitalId).orElseThrow(() -> new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id));
        Map<String, String> errorMap = new HashMap<>();

        if (request.getAppointmentDate() != null) {
            ZonedDateTime rawTime = Helpers.parseAndRoundToNearestTenMinutes(request.getAppointmentDate(), errorMap);
            appointment.setAppointmentDate(rawTime);
        }
        if (request.getStatus() != null) {
            appointment.setStatus(AppointmentStatus.valueOf(request.getStatus()));
        }
        if (!errorMap.isEmpty()) {
            throw new ResourceValidationException(errorMap);
        }
        appointment = appointmentRepository.saveAndFlush(appointment);
        return AppointmentResponseDTO.fromEntity(appointment);
    }

    @Transactional
    @Override
    public AppointmentResponseDTO updateAppointment(Long id, Long hospitalId, AppointmentUpdateRequestDTO request) {
        Appointment appointment = appointmentRepository.findByIdAndHospitalId(id, hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id));

        if(appointment.getStatus().toString().equals("COMPLETED")){
            throw new InvalidRequestException("Appointment is already completed");
        }
        // 1. Handle Prescription (Update existing or Create new)
        PrescriptionRequestDTO pDto = request.getPrescription();
        List<Prescription> prescription = appointment.getPrescription();
        prescription.add(prescriptionRepository.save(Prescription.toEntity(appointment, pDto)));

        // 2. Update Appointment fields
        appointment.setStatus(AppointmentStatus.valueOf(request.getStatus()));
        appointment.setTreatment(request.getTreatment());
        appointment.setNotes(request.getNotes());
        appointment.setPrescription(prescription);

        // saveAndFlush
        return AppointmentResponseDTO.fromEntity(appointmentRepository.saveAndFlush(appointment));
    }

    @Transactional
    @Override
    public void cancelAppointment(Long id, Long hospitalId) {
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
    public void deleteAppointment(Long id, Long hospitalId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id));
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
                .findByHospitalIdAndPatientId(hospitalId, patientId, pageable)
                .map(AppointmentResponseDTO::fromEntity);
    }


    @Override
    public Page<AppointmentListResponseDTO> getAppointmentsByHospitalAndStatusAndDate(Long hospitalId, AppointmentStatus status, int page, int size, String sortBy, LocalDate date) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        // 1. Create the start of the day in IST
        ZonedDateTime startOfDay = Helpers.getStartOfTheDay(date);

        // 2. Create the end of the day in IST
        ZonedDateTime endOfDay = Helpers.getEndOfTheDay(date);
        return appointmentRepository
                .findByHospitalIdAndStatusAndAppointmentDateBetween(hospitalId, status, startOfDay, endOfDay, pageable)
                .map(AppointmentListResponseDTO::fromEntity);
    }

    @Override
    public Page<AppointmentListResponseDTO> getAppointmentsByDoctorAndDate(Long doctorId, int page, int size, String sortBy, LocalDate date) {

        // 1. Create the start of the day in IST
        ZonedDateTime startOfDay = Helpers.getStartOfTheDay(date);

        // 2. Create the end of the day in IST
        ZonedDateTime endOfDay = Helpers.getEndOfTheDay(date);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return appointmentRepository
                .findByDoctorIdAndAppointmentDateBetween(doctorId, startOfDay, endOfDay, pageable)
                .map(AppointmentListResponseDTO::fromEntity);
    }

    @Override
    public Page<AppointmentResponseDTO> getAppointmentsByPatientAndDate(Long patientId, LocalDate date, int page, int size, String sortBy) {
        // 1. Create the start of the day in IST
        ZonedDateTime startOfDay = Helpers.getStartOfTheDay(date);

        // 2. Create the end of the day in IST
        ZonedDateTime endOfDay = Helpers.getEndOfTheDay(date);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Appointment> byPatientIdAndAppointmentDateBetween = appointmentRepository.findByPatientIdAndAppointmentDateBetween(patientId, startOfDay, endOfDay, pageable);
        return byPatientIdAndAppointmentDateBetween.map(AppointmentResponseDTO::fromEntity);
    }

    private Patient resolvePatient(Long hospitalId, PatientRequestDTO patientReq, Map<String, String> errorMap) {
        try {
            if (patientReq.getId() != null) {
                return patientRepository.findByIdAndHospitalId(patientReq.getId(), hospitalId)
                        .orElseThrow(() -> new ResourceNotFoundException(STR."Patient not found with ID: \{patientReq.getId()}"));
            } else {
                PatientResponseDTO newPatient = patientService.createPatientInHospital(hospitalId, patientReq);
                return patientRepository.findById(newPatient.id()).orElse(null);
            }
        } catch (Exception e) {
            errorMap.put("patient", e.getMessage());
            return null;
        }
    }

    private Doctor validateDoctor(Long hospitalId, Long doctorId, Map<String, String> errorMap) {
        if (doctorId == null) return null;
        return doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(doctorId, hospitalId)
                .orElseGet(() -> {
                    errorMap.put("doctorId", Constants.DOCTOR_NOT_FOUND + doctorId);
                    return null;
                });
    }

    private Department validateDepartment(Long deptId, Map<String, String> errorMap) {
        if (deptId == null) return null;
        return departmentRepository.findById(deptId)
                .orElseGet(() -> {
                    errorMap.put("departmentId", Constants.DEPARTMENT_NOT_FOUND + deptId);
                    return null;
                });
    }

    private Hospital validateHospital(Long hospitalId, Map<String, String> errorMap) {
        return hospitalRepository.findById(hospitalId)
                .orElseGet(() -> {
                    errorMap.put("hospitalId", Constants.HOSPITAL_NOT_FOUND + hospitalId);
                    return null;
                });
    }
}
