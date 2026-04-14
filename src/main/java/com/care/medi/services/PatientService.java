package com.care.medi.services;

import com.care.medi.dtos.request.InsuranceRequestDTO;
import com.care.medi.dtos.request.PatientRequestDTO;
import com.care.medi.dtos.request.PatientUpdateRequestDTO;
import com.care.medi.dtos.response.InsuranceResponseDTO;
import com.care.medi.dtos.response.PatientListResponseDTO;
import com.care.medi.dtos.response.PatientResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PatientService {
    Page<PatientListResponseDTO> getAllPatients(int page, int size, String sortBy);

    PatientResponseDTO getPatientByIdAndHospitalId(Long hospitalId,Long id);

    @Transactional
    PatientResponseDTO createPatientInHospital(Long hospitalId, PatientRequestDTO patient);

    @Transactional
    PatientResponseDTO updatePatientInHospital(Long patientId, Long hospitalId, PatientUpdateRequestDTO patientDTO);

    void deletePatientFromHospital(Long patientId, Long hospitalId);

    InsuranceResponseDTO assignInsurance(Long patientId, InsuranceRequestDTO insurance);

    List<InsuranceResponseDTO> getInsuranceByPatientId(Long patientId);

    Page<PatientListResponseDTO> getAllPatientsByHospital(Long hospitalId, Integer page, Integer size, String sortBy);
}
