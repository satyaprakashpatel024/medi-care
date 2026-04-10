package com.care.medi.services;

import com.care.medi.dtos.request.PatientRequestDTO;
import com.care.medi.dtos.request.PatientUpdateRequestDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import com.care.medi.dtos.response.InsuranceResponseDTO;
import com.care.medi.dtos.response.PatientResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.PatientRepository;
import com.care.medi.repository.UserRepository;
import com.care.medi.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private  final PatientRepository patientRepository;
    private final UserRepository userRepository;
    @Override
    public Page<PatientResponseDTO> getAllPatients(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Patient> all = patientRepository.findAll(pageable);
        return all.map(PatientResponseDTO::fromEntity);
    }

    @Override
    public PatientResponseDTO getPatientById(Long id) {
        Optional<Patient> byId = patientRepository.findById(id);
        if(byId.isEmpty()) {
            throw new ResourceNotFoundException(Constants.PATIENT_NOT_FOUND+ id);
        }
        return PatientResponseDTO.fromEntity(byId.get());
    }

    @Transactional
    @Override
    public PatientResponseDTO createPatient(PatientRequestDTO patient) {
        Users user = Users.builder()
                .email(patient.getEmail())
                .passwordHash("password")
                .role(Role.PATIENT)
                .isActive(true)
                .build();
        user = userRepository.save(user);
        Patient build = Patient.builder()
                .user(user)
                .phone(patient.getPhone())
                .emergencyContact(patient.getEmergencyContact())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(Gender.valueOf(patient.getGender()))
                .bloodGroup(BloodGroup.valueOf(patient.getBloodGroup()))
                .build();
        patientRepository.save(build);
        return null;
    }

    @Override
    public PatientResponseDTO updatePatient(Long id, PatientUpdateRequestDTO patient) {
        return null;
    }

    @Override
    public void deletePatient(Long id) {

    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public void assignInsurance(Long patientId, Long insuranceId) {

    }

    @Override
    public void removeInsurance(Long patientId, Long insuranceId) {

    }

    @Override
    public List<InsuranceResponseDTO> getInsuranceByPatientId(Long patientId) {
        return List.of();
    }

    @Override
    public void assignAppointment(Long patientId, Long appointmentId) {

    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsByPatientId(Long patientId) {
        return List.of();
    }
}
