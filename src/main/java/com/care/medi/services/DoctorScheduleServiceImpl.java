package com.care.medi.services;

import com.care.medi.dtos.request.DoctorScheduleRequestDTO;
import com.care.medi.dtos.response.DoctorScheduleResponseDTO;
import com.care.medi.entity.Doctor;
import com.care.medi.entity.DoctorSchedule;
import com.care.medi.exception.InvalidRequestException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.DoctorRepository;
import com.care.medi.repository.DoctorScheduleRepository;
import com.care.medi.repository.HospitalRepository;
import com.care.medi.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;

    @Override
    @Transactional
    public DoctorScheduleResponseDTO createOrUpdateSchedule(Long hospitalId, Long doctorId, DoctorScheduleRequestDTO request) {
        validateHospitalAndDoctor(hospitalId, doctorId);

        if (request.getWorkStartTime().isAfter(request.getWorkEndTime()) || request.getWorkStartTime().equals(request.getWorkEndTime())) {
            throw new InvalidRequestException("Work start time must be strictly before work end time.");
        }

        if (request.getBreakStartTime() != null && request.getBreakEndTime() != null) {
            if (request.getBreakStartTime().isAfter(request.getBreakEndTime())) {
                throw new InvalidRequestException("Break start time must be before break end time.");
            }
            if (request.getBreakStartTime().isBefore(request.getWorkStartTime()) || request.getBreakEndTime().isAfter(request.getWorkEndTime())) {
                throw new InvalidRequestException("Break time must be within working hours.");
            }
        }

        String workingDaysStr = (request.getWorkingDays() != null && !request.getWorkingDays().isEmpty())
                ? String.join(",", request.getWorkingDays().stream().map(String::toUpperCase).toList())
                : "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY";

        int slotDuration = 15;
        if (request.getSlotDurationMinutes() != null && request.getSlotDurationMinutes() > 0) {
            slotDuration = request.getSlotDurationMinutes();
        }

        DoctorSchedule schedule = doctorScheduleRepository.findByDoctorIdAndHospitalIdAndIsActiveTrue(doctorId, hospitalId)
                .orElseGet(() -> DoctorSchedule.builder()
                        .doctorId(doctorId)
                        .hospitalId(hospitalId)
                        .createdAt(ZonedDateTime.now(Constants.ZONE_ID))
                        .build());

        schedule.setWorkStartTime(request.getWorkStartTime());
        schedule.setWorkEndTime(request.getWorkEndTime());
        schedule.setBreakStartTime(request.getBreakStartTime());
        schedule.setBreakEndTime(request.getBreakEndTime());
        schedule.setSlotDurationMinutes(slotDuration);
        schedule.setWorkingDays(workingDaysStr);
        schedule.setActive(true);

        DoctorSchedule saved = doctorScheduleRepository.save(schedule);
        return DoctorScheduleResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorScheduleResponseDTO getScheduleByDoctorAndHospital(Long hospitalId, Long doctorId) {
        validateHospitalAndDoctor(hospitalId, doctorId);
        DoctorSchedule schedule = getDoctorScheduleEntityOrDefault(hospitalId, doctorId);
        return DoctorScheduleResponseDTO.fromEntity(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorSchedule getDoctorScheduleEntityOrDefault(Long hospitalId, Long doctorId) {
        Optional<DoctorSchedule> optionalSchedule = doctorScheduleRepository.findByDoctorIdAndHospitalIdAndIsActiveTrue(doctorId, hospitalId);
        return optionalSchedule.orElseGet(() -> buildDefaultSchedule(hospitalId, doctorId));
    }

    private DoctorSchedule buildDefaultSchedule(Long hospitalId, Long doctorId) {
        return DoctorSchedule.builder()
                .doctorId(doctorId)
                .hospitalId(hospitalId)
                .workStartTime(LocalTime.of(9, 0))
                .workEndTime(LocalTime.of(17, 0))
                .breakStartTime(LocalTime.of(13, 0))
                .breakEndTime(LocalTime.of(14, 0))
                .slotDurationMinutes(15)
                .workingDays("MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY")
                .isActive(true)
                .build();
    }

    private void validateHospitalAndDoctor(Long hospitalId, Long doctorId) {
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + hospitalId);
        }
        Optional<Doctor> doctorOpt = doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(doctorId, hospitalId);
        if (doctorOpt.isEmpty()) {
            throw new ResourceNotFoundException(String.format(Constants.DOCTOR_NOT_FOUND, doctorId, hospitalId));
        }
    }
}
