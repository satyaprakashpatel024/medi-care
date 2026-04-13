package com.care.medi.services;

import com.care.medi.dtos.request.InsuranceRequestDTO;
import com.care.medi.dtos.request.PatientRequestDTO;
import com.care.medi.dtos.request.PatientUpdateRequestDTO;
import com.care.medi.dtos.response.InsuranceResponseDTO;
import com.care.medi.dtos.response.PatientListResponseDTO;
import com.care.medi.dtos.response.PatientResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PatientService {
    Page<PatientListResponseDTO> getAllPatients(int page, int size, String sortBy);

    PatientResponseDTO getPatientById(Long id);

    PatientResponseDTO createPatient(PatientRequestDTO patient);

    PatientResponseDTO updatePatient(Long id, PatientUpdateRequestDTO patient);

    void deletePatient(Long id);

    InsuranceResponseDTO assignInsurance(Long patientId, InsuranceRequestDTO insurance);

    List<InsuranceResponseDTO> getInsuranceByPatientId(Long patientId);

    Page<PatientListResponseDTO> getAllPatientsByHospital(Long hospitalId, Integer page, Integer size, String sortBy);
}
