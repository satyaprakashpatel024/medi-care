package com.care.medi.services;

import com.care.medi.dtos.request.AppointmentRequestDTO;
import com.care.medi.dtos.request.AppointmentRescheduleDTO;
import com.care.medi.dtos.request.AppointmentUpdateRequestDTO;
import com.care.medi.dtos.request.PrescriptionRequestDTO;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.exception.ResourceValidationException;
import com.care.medi.repository.*;
import com.care.medi.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService{

    private  final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public Page<AppointmentListResponseDTO> getAllAppointments(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Appointment> all = appointmentRepository.findAll(pageable);
        return all.map(AppointmentListResponseDTO::fromEntity);
    }

    @Override
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {
        Map<String, String> errorMap = new HashMap<>();

        // 1. Validate Patient
        Optional<Patient> patientOpt = patientRepository.findById(request.getPatientId());
        if (patientOpt.isEmpty()) {
            errorMap.put("patientId", Constants.PATIENT_NOT_FOUND + request.getPatientId());
        }

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

        // 4. If the map has errors, throw a custom exception
        if (!errorMap.isEmpty()) {
            throw new ResourceValidationException(errorMap);
        }

        // 5. Proceed with creation if no errors
        Appointment appointment = Appointment.builder()
                .patient(patientOpt.orElse(null))
                .doctor(doctorOpt.orElse(null))
                .department(departmentOpt.orElse(null))
                .appointmentDate(request.getAppointmentDate())
                .status(AppointmentStatus.SCHEDULED)
                .createdAt(LocalDateTime.now())
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
        if(request.getAppointmentDate()!=null) {
            appointment.setAppointmentDate(request.getAppointmentDate());
        }
        if(request.getStatus()!=null) {
            appointment.setStatus(AppointmentStatus.valueOf(request.getStatus()));
        }
            appointment = appointmentRepository.saveAndFlush(appointment);
        return AppointmentResponseDTO.fromEntity(appointment);
    }

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
                    .createdAt(LocalDateTime.now())
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
    @SneakyThrows
    @Override
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + id));
        if(appointment.getStatus().equals(AppointmentStatus.SCHEDULED)||appointment.getStatus().equals(AppointmentStatus.NO_SHOW)){
            appointment.setStatus(AppointmentStatus.CANCELLED);
        }else{
            throw new ResourceNotFoundException(Constants.INVALID_STATUS + appointment.getStatus().name());
        }
        appointmentRepository.saveAndFlush(appointment);
    }

    @Override
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Override
    public Page<AppointmentListResponseDTO> getAppointmentsByDoctor(Long doctorId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return appointmentRepository.findByDoctorId(doctorId, pageable);
    }

    @Override
    public Page<AppointmentResponseDTO> getAppointmentsByPatient(Long patientId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Appointment> byPatientId = appointmentRepository.findByPatientId(patientId, pageable);
        return byPatientId.map(AppointmentResponseDTO::fromEntity);
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
