package com.care.medi.services;

import com.care.medi.dtos.request.PatientRequestDTO;
import com.care.medi.dtos.request.PatientUpdateRequestDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import com.care.medi.dtos.response.InsuranceResponseDTO;
import com.care.medi.dtos.response.PatientResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PatientService {
    Page<PatientResponseDTO> getAllPatients(int page, int size, String sortBy);
    PatientResponseDTO getPatientById(Long id);
    PatientResponseDTO createPatient(PatientRequestDTO patient);
    PatientResponseDTO updatePatient(Long id, PatientUpdateRequestDTO patient);
    void deletePatient(Long id);
    boolean existsById(Long id);
    void assignInsurance(Long patientId, Long insuranceId);
    void removeInsurance(Long patientId, Long insuranceId);
    List<InsuranceResponseDTO> getInsuranceByPatientId(Long patientId);
    void assignAppointment(Long patientId, Long appointmentId);
    List<AppointmentResponseDTO> getAppointmentsByPatientId(Long patientId);
}
