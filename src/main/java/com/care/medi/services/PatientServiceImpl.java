package com.care.medi.services;

import com.care.medi.dtos.request.InsuranceRequestDTO;
import com.care.medi.dtos.request.PatientRequestDTO;
import com.care.medi.dtos.request.PatientUpdateRequestDTO;
import com.care.medi.dtos.response.InsuranceResponseDTO;
import com.care.medi.dtos.response.PatientListResponseDTO;
import com.care.medi.dtos.response.PatientResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.DuplicateResourceException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.InsuranceRepository;
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

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final InsuranceRepository insuranceRepository;

    @Override
    public Page<PatientListResponseDTO> getAllPatients(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Patient> all = patientRepository.findAll(pageable);
        return all.map(PatientListResponseDTO::fromEntity);
    }

    @Override
    public PatientResponseDTO getPatientById(Long id) {
        Optional<Patient> byId = patientRepository.findById(id);
        if (byId.isEmpty()) {
            throw new ResourceNotFoundException(Constants.PATIENT_NOT_FOUND + id);
        }
        return PatientResponseDTO.fromEntity(byId.get());
    }

    @Transactional
    @Override
    public PatientResponseDTO createPatient(PatientRequestDTO patient) {
        if (userRepository.existsByEmail(patient.getEmail())) {
            throw new DuplicateResourceException(Constants.DUPLICATE_EMAIL + patient.getEmail());
        }
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
                .gender(Gender.valueOf(patient.getGender().toUpperCase()))
                .bloodGroup(BloodGroup.valueOf(patient.getBloodGroup().toUpperCase()))
                .build();
        build = patientRepository.save(build);
        return PatientResponseDTO.fromEntity(build);
    }

    @Transactional
    @Override
    public PatientResponseDTO updatePatient(Long id, PatientUpdateRequestDTO patientDTO) {
        // 1. Fetch the existing entity
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.PATIENT_NOT_FOUND + id));

        // 2. Conditionally update fields (Check for null before setting)
        if (patientDTO.getFirstName() != null) {
            existingPatient.setFirstName(patientDTO.getFirstName());
        }
        if (patientDTO.getLastName() != null) {
            existingPatient.setLastName(patientDTO.getLastName());
        }
        if (patientDTO.getDateOfBirth() != null) {
            existingPatient.setDateOfBirth(patientDTO.getDateOfBirth());
        }
        if (patientDTO.getGender() != null) {
            existingPatient.setGender(Gender.valueOf(patientDTO.getGender().toUpperCase()));
        }
        if (patientDTO.getPhone() != null) {
            existingPatient.setPhone(patientDTO.getPhone());
        }
        if (patientDTO.getEmergencyContact() != null) {
            existingPatient.setEmergencyContact(patientDTO.getEmergencyContact());
        }
        if (patientDTO.getBloodType() != null) {
            existingPatient.setBloodGroup(BloodGroup.valueOf(patientDTO.getBloodType().toUpperCase()));
        }

        // 3. Save the updated entity
        // Note: With @Transactional, changes are auto-flushed, but calling save is clear practice.
        Patient updatedPatient = patientRepository.save(existingPatient);

        // 4. Return the converted Response DTO
        return PatientResponseDTO.fromEntity(updatedPatient);
    }

    @Override
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    @Transactional
    @Override
    public InsuranceResponseDTO assignInsurance(Long patientId, InsuranceRequestDTO request) {
        Optional<Patient> byId = patientRepository.findById(patientId);
        if (byId.isEmpty()) {
            throw new ResourceNotFoundException(Constants.PATIENT_NOT_FOUND + patientId);
        }

        Insurance insurance = Insurance.builder()
                .providerName(request.getProviderName())
                .policyNumber(request.getPolicyNumber())
                .patient(byId.get())
                .status(InsuranceStatus.valueOf(request.getInsuranceStatus().toUpperCase()))
                .coverageAmount(request.getCoverageAmount())
                .deductible(request.getDeductible())
                .policyType(request.getPolicyType().toUpperCase())
                .providerContactEmail(request.getProviderContactEmail())
                .providerPhoneNumber(request.getProviderPhoneNumber())
                .expiryDate(request.getExpiryDate())
                .startDate(request.getStartDate())
                .build();
        insurance = insuranceRepository.save(insurance);
        return InsuranceResponseDTO.fromEntity(insurance);
    }

    @Override
    public List<InsuranceResponseDTO> getInsuranceByPatientId(Long patientId) {
        List<Insurance> byPatientId = insuranceRepository.findByPatientId(patientId);
        return byPatientId.stream().map(InsuranceResponseDTO::fromEntity).toList();
    }

}
