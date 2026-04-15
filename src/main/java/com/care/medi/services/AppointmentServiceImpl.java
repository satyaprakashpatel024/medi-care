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

import java.time.*;
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
        ZonedDateTime startOfDay = Helpers.getStartOfTheDay(date);

        // 2. Create the end of the day in IST
        ZonedDateTime endOfDay = Helpers.getEndOfTheDay(date);

        Page<Appointment> all = appointmentRepository.findByHospitalIdAndAppointmentDateBetween(
                hospitalId, startOfDay, endOfDay, pageable);
        return all.map(AppointmentListResponseDTO::fromEntity);
    }

    @Override
    // Appointment slots are every 10 minutes: 09:00, 09:10, 09:20 ...
    @Transactional // Always use Transactional when saving multiple entities
    public AppointmentResponseDTO createAppointment(Long hospitalId, AppointmentRequestDTO request) {
        Map<String, String> errorMap = new HashMap<>();

        // 1. Normalize time
        ZonedDateTime normalizedTime = roundToNearestTenMinutes(request.getAppointmentDate());

        // 2. Resolve Patient (Existing vs New)
        Patient patientEntity = null;
        try {
            if (request.getPatient().getId() != null) {
                patientEntity = patientRepository.findByIdAndHospitalId(request.getPatient().getId(),hospitalId)
                        .orElseThrow(() -> new ResourceNotFoundException(STR."Patient not found with ID: \{request.getPatient().getId()}"));
            } else {
                // Create new patient and get the entity
                PatientResponseDTO newPatientResp = patientService.createPatientInHospital(hospitalId, request.getPatient());
                patientEntity = patientRepository.findById(newPatientResp.id()).orElse(null);
            }
        } catch (Exception e) {
            errorMap.put("patient", e.getMessage());
        }

        // 3. Validate Doctor
        Doctor doctor = request.getDoctorId() == null ? null :
                doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(request.getDoctorId(),hospitalId).orElse(null);
        if (doctor == null) errorMap.put("doctorId", Constants.DOCTOR_NOT_FOUND + request.getDoctorId());

        // 4. Validate Department
        Department department = request.getDepartmentId() == null ? null :
                departmentRepository.findById(request.getDepartmentId()).orElse(null);
        if (department == null) errorMap.put("departmentId", Constants.DEPARTMENT_NOT_FOUND + request.getDepartmentId());

        // 5. Validate Hospital
        Hospital hospital = hospitalRepository.findById(hospitalId).orElse(null);
        if (hospital == null) errorMap.put("hospitalId", Constants.HOSPITAL_NOT_FOUND + hospitalId);

        // 6. Final Check: Throw if any errors collected
        if (!errorMap.isEmpty() || patientEntity == null) {
            if (patientEntity == null && !errorMap.containsKey("patient")) {
                errorMap.put("patient", "Patient could not be resolved.");
            }
            throw new ResourceValidationException(errorMap);
        }

        // 7. Build and persist
        Appointment appointment = Appointment.builder()
                .patient(patientEntity)
                .doctor(doctor)
                .department(department)
                .hospital(hospital)
                .appointmentDate(normalizedTime)
                .status(AppointmentStatus.SCHEDULED)
                .createdAt(OffsetDateTime.now())
                .build();

        return AppointmentResponseDTO.fromEntity(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponseDTO getAppointmentByIdAndHospital(Long id,Long hospitalId) {
        Optional<Appointment> byId = appointmentRepository.findByIdAndHospitalId(id,hospitalId);
        if (byId.isEmpty()) {
            throw new ResourceNotFoundException(STR."\{Constants.APPOINTMENT_NOT_FOUND}\{id} And Hospital Id : \{hospitalId}");
        }
        return AppointmentResponseDTO.fromEntity(byId.get());
    }

    @Transactional
    @Override
    public AppointmentResponseDTO rescheduleAppointment(Long id, AppointmentRescheduleDTO request,Long hospitalId) {
        Optional<Appointment> byId = appointmentRepository.findByIdAndHospitalId(id,hospitalId);
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
    public AppointmentResponseDTO updateAppointment(Long id, Long hospitalId, AppointmentUpdateRequestDTO request) {
        Appointment appointment = appointmentRepository.findByIdAndHospitalId(id,hospitalId)
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
    public void cancelAppointment(Long id,Long hospitalId) {
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
    public void deleteAppointment(Long id,Long hospitalId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id));
        appointment.setStatus(AppointmentStatus.CANCELLED);
    }

    public void deleteAppointmentById(Long id,Long hospitalId) {
        appointmentRepository.deleteById(id);
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
                .findByHospitalIdAndStatusAndAppointmentDateBetween(hospitalId,status, startOfDay, endOfDay, pageable)
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

    /**
     * Rounds a given datetime to the nearest 10-minute boundary.
     * Examples:
     *   10:07 → 10:10
     *   10:04 → 10:00
     *   10:05 → 10:10  (rounds up on exact half)
     */
    private ZonedDateTime roundToNearestTenMinutes(ZonedDateTime dateTime) {
        int minute = dateTime.getMinute();
        int roundedMinute = (int) (Math.round(minute / 10.0) * 10);

        if (roundedMinute == 60) {
            // e.g. 10:55 rounds to 11:00
            return dateTime.withMinute(0).withSecond(0).withNano(0).plusHours(1);
        }

        return dateTime.withMinute(roundedMinute).withSecond(0).withNano(0);
    }
}
