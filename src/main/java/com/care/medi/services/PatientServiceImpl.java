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
    private final HospitalService hospitalService;

    @Transactional(readOnly = true)
    @Override
    public PatientResponseDTO getPatientByIdAndHospitalId(Long patientId, Long hospitalId) {
        Optional<Patient> byId = patientRepository.findByIdAndHospitalId(patientId, hospitalId);
        if (byId.isEmpty()) {
            throw new ResourceNotFoundException(Constants.PATIENT_NOT_FOUND + patientId);
        }
        return PatientResponseDTO.fromEntity(byId.get());
    }

    @Transactional
    @Override
    public PatientResponseDTO createPatientInHospital(Long hospitalId, PatientRequestDTO patient) {
        if (userRepository.existsByEmail(patient.getEmail())) {
            throw new DuplicateResourceException(Constants.DUPLICATE_EMAIL + patient.getEmail());
        }
        Users user = Users.toEntity(patient.getEmail(), "default", Role.PATIENT);
        user = userRepository.save(user);
        Patient save = patientRepository.save(Patient.toEntity(patient, user));
        return PatientResponseDTO.fromEntity(save);
    }

    @Transactional
    @Override
    public PatientResponseDTO updatePatientInHospital(Long patientId, Long hospitalId, PatientUpdateRequestDTO patientDTO) {
        // 1. Fetch the existing entity
        Patient existingPatient = patientRepository.findByIdAndHospitalId(patientId, hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.PATIENT_NOT_FOUND + patientId));

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
        Patient updatedPatient = patientRepository.saveAndFlush(existingPatient);

        // 4. Return the converted Response DTO
        return PatientResponseDTO.fromEntity(updatedPatient);
    }

    @Override
    public void deletePatientFromHospital(Long patientId, Long hospitalId) {
        patientRepository.deleteByIdAndHospitalId(patientId, hospitalId);
    }

    @Transactional
    @Override
    public InsuranceResponseDTO assignInsurance(Long patientId, Long hospitalId, InsuranceRequestDTO request) {
        Patient byId = patientRepository.findByIdAndHospitalId(patientId, hospitalId).orElseThrow(()-> new ResourceNotFoundException(Constants.PATIENT_NOT_FOUND + patientId));

        if (insuranceRepository.existsByPolicyNumber(request.getPolicyNumber())) {
            throw new DuplicateResourceException(Constants.DUPLICATE_POLICY);
        }

        Insurance insurance = Insurance.toEntity(request,byId);
        insurance = insuranceRepository.save(insurance);
        return InsuranceResponseDTO.fromEntity(insurance);
    }

    @Override
    public List<InsuranceResponseDTO> getInsuranceByPatientId(Long patientId, Long hospitalId) {
        boolean b = patientRepository.existsById(patientId);
        if (!b) {
            throw new ResourceNotFoundException(Constants.PATIENT_NOT_FOUND + patientId);
        }
        List<Insurance> byPatientId = insuranceRepository.findByPatientId(patientId);
        if (byPatientId.isEmpty()) {
            throw new ResourceNotFoundException(Constants.INSURANCE_NOT_FOUND);
        }
        return byPatientId.stream().map(InsuranceResponseDTO::fromEntity).toList();
    }

    @Override
    public Page<PatientListResponseDTO> getAllPatientsByHospital(Long hospitalId, Integer page, Integer size, String sortBy) {
        if (!hospitalService.existsById(hospitalId)) {
            throw new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + hospitalId);
        }
        return patientRepository.findAllByHospitalId(hospitalId, PageRequest.of(page, size, Sort.by(sortBy)));
    }

    @Override
    public boolean existsByIdAndHospitalId(Long patientId, Long hospitalId) {
        return patientRepository.existsByIdAndHospitalId(patientId, hospitalId);
    }

}
