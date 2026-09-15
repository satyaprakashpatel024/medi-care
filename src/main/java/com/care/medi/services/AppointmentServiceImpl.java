package com.care.medi.services;

import com.care.medi.dtos.EmailNotificationEvent;
import com.care.medi.dtos.request.*;
import com.care.medi.dtos.response.*;
import com.care.medi.entity.*;
import com.care.medi.exception.InvalidRequestException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.exception.ResourceValidationException;
import com.care.medi.repository.*;
import com.care.medi.services.kafka.EmailNotificationProducer;
import com.care.medi.utils.Constants;
import com.care.medi.utils.Helpers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DepartmentRepository departmentRepository;
    private final PatientServiceImpl patientService;
    private final HospitalRepository hospitalRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final EmailNotificationProducer emailNotificationProducer;
    private final DoctorScheduleService doctorScheduleService;

    @Override
    public Optional<Appointment> findByIdAndStatusIn(Long id, Collection<AppointmentStatus> statuses) {
        return appointmentRepository.findByIdAndStatusIn(id, statuses);
    }

    @Override
    public boolean isAppointmentContextValid(Long appointmentId, Long hospitalId, Long doctorId, Long patientId) {
        return appointmentRepository.isAppointmentContextValid(appointmentId, hospitalId, doctorId, patientId);
    }

    @Override
    public boolean existsByIdAndHospitalId(Long id, Long hospitalId) {
        return appointmentRepository.existsByIdAndHospitalId(id, hospitalId);
    }

    @Override
    public boolean existsByIdAndDoctorIdAndHospitalId(Long appointmentId, Long doctorId, Long hospitalId) {
        return appointmentRepository.existsByIdAndDoctorIdAndHospitalId(appointmentId, doctorId, hospitalId);
    }

    @Override
    public Page<AppointmentSummaryResponseDTO> getAllAppointmentsByHospitalAndDate(
            Long hospitalId, Integer page, Integer size, String sortBy, LocalDate date) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        LocalDate startOfDay = date.atStartOfDay().toLocalDate();
        LocalDate endOfDay = startOfDay.plusDays(1);
        return appointmentRepository.findByHospitalIdAndAppointmentDateBetween(hospitalId, startOfDay, endOfDay, pageable);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO createAppointment(Long hospitalId, AppointmentRequestDTO request) {
        Map<String, String> errorMap = new HashMap<>();

        // 1. Validate Hospital
        validateHospital(hospitalId, errorMap);

        // 2. Validate Date and Time
        LocalDate date = Helpers.parseAppointmentDate(request.getAppointmentDate(), errorMap);
        LocalTime time = Helpers.parseAppointmentTime(request.getAppointmentTime(), errorMap);

        // 3. Validate appointment slot using Doctor's configured slot duration
        int slotDuration = 15;
        if (request.getDoctorId() != null) {
            DoctorSchedule schedule = doctorScheduleService.getDoctorScheduleEntityOrDefault(hospitalId, request.getDoctorId());
            if (schedule != null && schedule.getSlotDurationMinutes() != null && schedule.getSlotDurationMinutes() > 0) {
                slotDuration = schedule.getSlotDurationMinutes();
            }
        }

        if (date != null && time != null && request.getDoctorId() != null) {
            boolean b = appointmentRepository.existsConflictingAppointment(request.getDoctorId(), hospitalId, date, time, time.plusMinutes(slotDuration));
            if (b) {
                errorMap.put("conflictingAppointment", Constants.CONFLICTING_APPOINTMENT);
            }
        }

        // 4. Resolve Patient (Existing or New)
        Patient patientEntity = resolvePatient(hospitalId, request.getPatient(), errorMap);

        // 5. Validate Domain Entities Doctor
        Doctor doctor = validateDoctor(hospitalId, request.getDoctorId(), errorMap);

        // 6. Validate Department
        Department department = validateDepartment(request.getDepartmentId(), errorMap);

        // 7. Guard Clause: Throw if any errors collected
        if (!errorMap.isEmpty() || patientEntity == null) {
            throw new ResourceValidationException(errorMap);
        }

        // 8. Build and persist
        Appointment appointment = Appointment.toEntity(patientEntity, doctor, department, hospitalId, date, time, slotDuration);
        Appointment save = appointmentRepository.save(appointment);

        // 9. Send confirmation Email
        String recipientEmail = Helpers.getRecipientEmail(patientEntity);
        emailNotificationProducer.sendEmailNotification(
                new EmailNotificationEvent(
                        recipientEmail,
                        String.format("%s %s", patientEntity.getFirstName(), patientEntity.getLastName()),
                        String.format("%s %s", doctor.getFirstName(), doctor.getLastName()),
                        save.getAppointmentDate().toString(),
                        save.getStartTime().format(Constants.HUMAN_TIME_FORMAT),
                        save.getId()
                )
        );
        return AppointmentResponseDTO.fromEntity(save);
    }

    @Override
    public AppointmentResponseDTO getAppointmentByIdAndHospital(Long id, Long hospitalId) {
        Optional<Appointment> byId = appointmentRepository.findByIdAndHospitalId(id, hospitalId);
        if (byId.isEmpty()) {
            log.warn(String.format("Appointment with Id %s and Hospital Id %s not found.", id, hospitalId));
            throw new ResourceNotFoundException(String.format("%s %%s And Hospital Id : %%s".formatted(Constants.APPOINTMENT_NOT_FOUND), id, hospitalId));
        }
        return AppointmentResponseDTO.fromEntity(byId.get());
    }

    @Transactional
    @Override
    public AppointmentResponseDTO rescheduleAppointment(Long id, AppointmentRescheduleDTO request, Long hospitalId) {
        Map<String, String> errorMap = new HashMap<>();
        Appointment appointment = validateAppointmentForReschedule(id, hospitalId, request, errorMap);
        if (!errorMap.isEmpty()) {
            throw new ResourceValidationException(errorMap);
        }
        appointment = appointmentRepository.saveAndFlush(appointment);
        Patient patient = appointment.getPatient();
        String recipientEmail = Helpers.getRecipientEmail(patient);

        String patientFullName = String.format("%s %s", patient.getFirstName(), patient.getLastName());
        String doctorFullName = String.format("%s %s", appointment.getDoctor().getFirstName(), appointment.getDoctor().getLastName());
        emailNotificationProducer.sendEmailNotification(
                new EmailNotificationEvent(
                        recipientEmail,
                        patientFullName,
                        doctorFullName,
                        appointment.getAppointmentDate().toString(),
                        appointment.getStartTime().format(Constants.HUMAN_TIME_FORMAT),
                        appointment.getId()
                )
        );
        return AppointmentResponseDTO.fromEntity(appointment);
    }

    @Transactional
    @Override
    public AppointmentResponseDTO updateAppointment(Long id, Long hospitalId, AppointmentUpdateRequestDTO request) {
        Appointment appointment = appointmentRepository.findByIdAndHospitalId(id, hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidRequestException(Constants.INVALID_REQUEST_APPOINTMENT_IS_COMPLETED);
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

        return AppointmentResponseDTO.fromEntity(appointmentRepository.saveAndFlush(appointment));
    }

    @Transactional
    @Override
    public AppointmentResponseDTO updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id));
        appointment.setStatus(status);
        return AppointmentResponseDTO.fromEntity(appointmentRepository.saveAndFlush(appointment));
    }

    @Override
    public void cancelAppointment(Long id, Long hospitalId) {
        Appointment appointment = appointmentRepository.findByIdAndHospitalId(id, hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id));

        switch (appointment.getStatus()) {
            case SCHEDULED, NO_SHOW -> {
                appointment.setStatus(AppointmentStatus.CANCELLED);
                appointmentRepository.save(appointment);
            }
            case COMPLETED -> {
                throw new InvalidRequestException(Constants.INVALID_REQUEST_APPOINTMENT_IS_COMPLETED);
            }
            case CANCELLED -> {
                throw new InvalidRequestException(Constants.INVALID_REQUEST_APPOINTMENT_IS_CANCELLED);
            }
            default ->
                    throw new InvalidRequestException(Constants.INVALID_APPOINTMENT_STATUS + appointment.getStatus().name());
        }
        emailNotificationProducer.sendEmailNotification(
                new EmailNotificationEvent(
                        Helpers.getRecipientEmail(appointment.getPatient()),
                        String.format("%s %s", appointment.getPatient().getFirstName(), appointment.getPatient().getLastName()),
                        String.format("%s %s", appointment.getDoctor().getFirstName(), appointment.getDoctor().getLastName()),
                        appointment.getAppointmentDate().toString(),
                        appointment.getStartTime().format(Constants.HUMAN_TIME_FORMAT),
                        appointment.getId()
                )
        );
    }

    @Transactional
    @Override
    public void deleteAppointment(Long id, Long hospitalId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.delete(appointment);
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
        LocalDate startOfDay = Helpers.getStartOfTheDay(date);
        LocalDate endOfDay = Helpers.getEndOfTheDay(date);
        return appointmentRepository
                .findByHospitalIdAndStatusAndAppointmentDateBetween(hospitalId, status, startOfDay, endOfDay, pageable);
    }

    @Override
    public Page<AppointmentListResponseDTO> getAppointmentsByDoctorAndHospitalIdAndDate(Long doctorId, Long hospitalId, int page, int size, String sortBy, LocalDate date) {
        LocalDate startOfDay = Helpers.getStartOfTheDay(date);
        LocalDate endOfDay = Helpers.getEndOfTheDay(date);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return appointmentRepository
                .findByDoctorIdAndHospitalIdAndAppointmentDateBetween(doctorId, hospitalId, startOfDay, endOfDay, pageable)
                .map(AppointmentListResponseDTO::fromEntity);
    }

    @Override
    public Page<AppointmentResponseDTO> getAppointmentsByPatientAndDate(Long patientId, LocalDate date, int page, int size, String sortBy) {
        LocalDate startOfDay = date.atStartOfDay().toLocalDate();
        LocalDate endOfDay = startOfDay.plusDays(1);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Appointment> byPatientIdAndAppointmentDateBetween = appointmentRepository.findByPatientIdAndAppointmentDateBetween(patientId, startOfDay, endOfDay, pageable);
        return byPatientIdAndAppointmentDateBetween.map(AppointmentResponseDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDaySlotsResponseDTO getAvailableSlots(Long hospitalId, Long doctorId, LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now(Constants.ZONE_ID);
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + hospitalId);
        }
        Doctor doctor = doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(doctorId, hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(Constants.DOCTOR_NOT_FOUND, doctorId, hospitalId)));

        DoctorSchedule schedule = doctorScheduleService.getDoctorScheduleEntityOrDefault(hospitalId, doctorId);
        int slotDuration = 15;
        if (schedule != null && schedule.getSlotDurationMinutes() != null && schedule.getSlotDurationMinutes() > 0) {
            slotDuration = schedule.getSlotDurationMinutes();
        }

        String dayOfWeekStr = targetDate.getDayOfWeek().name();
        String workingDaysRaw = (schedule != null) ? schedule.getWorkingDays() : null;
        List<String> workingDays = (workingDaysRaw != null && !workingDaysRaw.isBlank())
                ? Arrays.stream(workingDaysRaw.split(",")).map(String::trim).map(String::toUpperCase).toList()
                : List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY");

        boolean isWorkingDay = workingDays.contains(dayOfWeekStr);

        List<Appointment> bookedAppointments = isWorkingDay
                ? appointmentRepository.findByDoctorIdAndHospitalIdAndAppointmentDateAndStatusNot(doctorId, hospitalId, targetDate, AppointmentStatus.CANCELLED)
                : List.of();

        LocalTime workStart = (schedule != null && schedule.getWorkStartTime() != null) ? schedule.getWorkStartTime() : LocalTime.of(9, 0);
        LocalTime workEnd = (schedule != null && schedule.getWorkEndTime() != null) ? schedule.getWorkEndTime() : LocalTime.of(17, 0);
        LocalTime breakStart = (schedule != null) ? schedule.getBreakStartTime() : null;
        LocalTime breakEnd = (schedule != null) ? schedule.getBreakEndTime() : null;

        LocalTime currentTimeInZone = LocalTime.now(Constants.ZONE_ID);
        LocalDate todayInZone = LocalDate.now(Constants.ZONE_ID);

        List<AppointmentSlotDTO> slots = new ArrayList<>();
        int availableCount = 0;

        if (!isWorkingDay) {
            LocalTime tempStart = workStart;
            while (!tempStart.plusMinutes(slotDuration).isAfter(workEnd)) {
                LocalTime tempEnd = tempStart.plusMinutes(slotDuration);
                String timeRangeFormatted = String.format("%s - %s",
                        tempStart.format(Constants.HUMAN_TIME_FORMAT),
                        tempEnd.format(Constants.HUMAN_TIME_FORMAT));
                slots.add(AppointmentSlotDTO.builder()
                        .startTime(tempStart)
                        .endTime(tempEnd)
                        .available(false)
                        .status("NON_WORKING_DAY")
                        .formattedTimeRange(timeRangeFormatted)
                        .build());
                tempStart = tempEnd;
            }
        } else {
            LocalTime tempStart = workStart;
            while (!tempStart.plusMinutes(slotDuration).isAfter(workEnd)) {
                LocalTime tempEnd = tempStart.plusMinutes(slotDuration);
                final LocalTime finalStart = tempStart;
                final LocalTime finalEnd = tempEnd;

                String slotStatus = "AVAILABLE";
                boolean isAvailable = true;

                if (breakStart != null && breakEnd != null && finalStart.compareTo(breakEnd) < 0 && finalEnd.compareTo(breakStart) > 0) {
                    slotStatus = "BREAK";
                    isAvailable = false;
                } else if (targetDate.equals(todayInZone) && finalStart.isBefore(currentTimeInZone)) {
                    slotStatus = "PAST_TIME";
                    isAvailable = false;
                } else {
                    boolean isBooked = bookedAppointments.stream().anyMatch(appt ->
                            appt.getStartTime().compareTo(finalEnd) < 0 && appt.getEndTime().compareTo(finalStart) > 0
                    );
                    if (isBooked) {
                        slotStatus = "BOOKED";
                        isAvailable = false;
                    }
                }

                if (isAvailable) {
                    availableCount++;
                }

                String timeRangeFormatted = String.format("%s - %s",
                        tempStart.format(Constants.HUMAN_TIME_FORMAT),
                        tempEnd.format(Constants.HUMAN_TIME_FORMAT));

                slots.add(AppointmentSlotDTO.builder()
                        .startTime(tempStart)
                        .endTime(tempEnd)
                        .available(isAvailable)
                        .status(slotStatus)
                        .formattedTimeRange(timeRangeFormatted)
                        .build());

                tempStart = tempEnd;
            }
        }

        String doctorFullName = String.format("%s %s", doctor.getFirstName(), doctor.getLastName());

        return DoctorDaySlotsResponseDTO.builder()
                .doctorId(doctorId)
                .doctorName(doctorFullName)
                .hospitalId(hospitalId)
                .date(targetDate)
                .dayOfWeek(dayOfWeekStr)
                .slotDurationMinutes(slotDuration)
                .totalSlots(slots.size())
                .availableSlotsCount(availableCount)
                .slots(slots)
                .build();
    }

    private Patient resolvePatient(Long hospitalId, PatientRequestDTO patientReq, Map<String, String> errorMap) {
        try {
            if (patientReq.getId() != null) {
                return patientRepository.findByIdAndHospitalId(patientReq.getId(), hospitalId)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("Patient not found with ID: %s", patientReq.getId())));
            } else {
                PatientResponseDTO newPatient = patientService.createPatientInHospital(hospitalId, patientReq);
                return patientRepository.findById(newPatient.id()).orElseThrow(() -> new ResourceNotFoundException(String.format("Patient not found with ID: %s", newPatient.id())));
            }
        } catch (Exception e) {
            log.warn(Constants.LOG_SERVICE_EXCEPTION, "AppointmentServiceImpl.resolvePatient", e.getMessage(), e);
            errorMap.put("patient", e.getMessage());
            return null;
        }
    }

    private Doctor validateDoctor(Long hospitalId, Long doctorId, Map<String, String> errorMap) {
        if (doctorId == null) return null;
        return doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(doctorId, hospitalId)
                .orElseGet(() -> {
                    errorMap.put("doctorId", String.format(Constants.DOCTOR_NOT_FOUND, doctorId, hospitalId));
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

    private void validateHospital(Long hospitalId, Map<String, String> errorMap) {
        if (!hospitalRepository.existsById(hospitalId)) {
            errorMap.put("hospitalId", Constants.HOSPITAL_NOT_FOUND + hospitalId);
        }
    }

    public boolean checkForConflictingAppointment(Long doctorId, Long hospitalId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return appointmentRepository.existsConflictingAppointment(doctorId, hospitalId, date, startTime, endTime);
    }

    private Appointment validateAppointmentForReschedule(Long id, Long hospitalId, AppointmentRescheduleDTO request, Map<String, String> errorMap) {
        try {
            Appointment appointment = appointmentRepository.findByIdAndHospitalId(id, hospitalId)
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id));
            LocalDate rawDate = null;
            LocalTime rawTime = null;
            if (request.getAppointmentDate() != null) {
                rawDate = Helpers.parseAppointmentDate(request.getAppointmentDate(), errorMap);
            }
            if (request.getAppointmentTime() != null) {
                rawTime = Helpers.parseAppointmentTime(request.getAppointmentTime(), errorMap);
            }
            if (rawDate == null || rawTime == null) {
                errorMap.put("appointmentDateTime", "Both appointmentDate and appointmentTime are required for rescheduling.");
                return null;
            }

            DoctorSchedule doctorSchedule = doctorScheduleService.getDoctorScheduleEntityOrDefault(hospitalId, appointment.getDoctor().getId());
            int slotDuration = 15;
            if (doctorSchedule != null && doctorSchedule.getSlotDurationMinutes() != null && doctorSchedule.getSlotDurationMinutes() > 0) {
                slotDuration = doctorSchedule.getSlotDurationMinutes();
            }

            boolean b = appointmentRepository.existsConflictingAppointment(
                    appointment.getDoctor().getId(),
                    hospitalId,
                    rawDate,
                    rawTime,
                    rawTime.plusMinutes(slotDuration));
            if (b) {
                errorMap.put("conflictingAppointment", Constants.CONFLICTING_APPOINTMENT);
                return null;
            }
            appointment.setAppointmentDate(rawDate);
            appointment.setStartTime(rawTime);
            appointment.setEndTime(rawTime.plusMinutes(slotDuration));
            if (request.getStatus() != null) {
                appointment.setStatus(AppointmentStatus.valueOf(request.getStatus()));
            }
            return appointment;
        } catch (ResourceNotFoundException ex) {
            log.warn(Constants.LOG_SERVICE_EXCEPTION, "AppointmentServiceImpl.validateAppointmentForReschedule", ex.getMessage(), ex);
            errorMap.put("appointment", ex.getMessage());
            return null;
        }
    }
}
