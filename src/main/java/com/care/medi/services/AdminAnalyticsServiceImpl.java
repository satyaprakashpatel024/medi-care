package com.care.medi.services;

import com.care.medi.dtos.response.AppointmentAnalyticsResponseDTO;
import com.care.medi.dtos.response.HospitalStatsResponseDTO;
import com.care.medi.dtos.response.SystemKpiResponseDTO;
import com.care.medi.entity.AppointmentStatus;
import com.care.medi.entity.Hospital;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.*;
import com.care.medi.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {

    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final AppointmentRepository appointmentRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public SystemKpiResponseDTO getSystemKpis() {
        LocalDate today = LocalDate.now(Constants.ZONE_ID);
        return SystemKpiResponseDTO.builder()
                .totalHospitals(hospitalRepository.count())
                .totalDoctors(doctorRepository.countByIsActiveTrue())
                .totalPatients(patientRepository.count())
                .totalStaff(staffRepository.count())
                .totalAppointmentsToday(appointmentRepository.countByAppointmentDate(today))
                .totalDepartments(departmentRepository.count())
                .build();
    }

    @Override
    public HospitalStatsResponseDTO getHospitalStats(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + hospitalId));

        LocalDate today = LocalDate.now(Constants.ZONE_ID);

        return HospitalStatsResponseDTO.builder()
                .hospitalId(hospital.getId())
                .hospitalName(hospital.getName())
                .activeDoctorsCount(doctorRepository.countByHospitalIdAndIsActiveTrue(hospitalId))
                .totalPatientsCount(patientRepository.countByHospitalId(hospitalId))
                .totalStaffCount(staffRepository.countByHospitalId(hospitalId))
                .appointmentsTodayCount(appointmentRepository.countByHospitalIdAndAppointmentDate(hospitalId, today))
                .completedAppointmentsCount(appointmentRepository.countByHospitalIdAndStatus(hospitalId, AppointmentStatus.COMPLETED))
                .build();
    }

    @Override
    public AppointmentAnalyticsResponseDTO getAppointmentAnalytics(Long hospitalId) {
        if (hospitalId != null && !hospitalRepository.existsById(hospitalId)) {
            throw new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + hospitalId);
        }

        long total = hospitalId != null ? appointmentRepository.countByHospitalId(hospitalId) : appointmentRepository.count();
        long scheduled = hospitalId != null ? appointmentRepository.countByHospitalIdAndStatus(hospitalId, AppointmentStatus.SCHEDULED) : 0;
        long confirmed = hospitalId != null ? appointmentRepository.countByHospitalIdAndStatus(hospitalId, AppointmentStatus.SCHEDULED) : 0;
        long completed = hospitalId != null ? appointmentRepository.countByHospitalIdAndStatus(hospitalId, AppointmentStatus.COMPLETED) : 0;
        long cancelled = hospitalId != null ? appointmentRepository.countByHospitalIdAndStatus(hospitalId, AppointmentStatus.CANCELLED) : 0;

        return AppointmentAnalyticsResponseDTO.builder()
                .hospitalId(hospitalId)
                .totalAppointments(total)
                .scheduledCount(scheduled)
                .confirmedCount(confirmed)
                .completedCount(completed)
                .cancelledCount(cancelled)
                .build();
    }
}
