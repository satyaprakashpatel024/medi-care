package com.care.medi.services;

import com.care.medi.dtos.request.DispenseRequestDTO;
import com.care.medi.dtos.request.MedicationRequestDTO;
import com.care.medi.dtos.response.DispenseResponseDTO;
import com.care.medi.dtos.response.MedicationResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PharmacyService {
    MedicationResponseDTO addMedication(MedicationRequestDTO request);

    MedicationResponseDTO updateMedication(Long id, MedicationRequestDTO request);

    Page<MedicationResponseDTO> getAllMedications(int page, int size, String sortBy);

    List<MedicationResponseDTO> getLowStockMedications(Integer threshold);

    DispenseResponseDTO dispenseMedications(DispenseRequestDTO request);

    Page<DispenseResponseDTO> getPatientDispenseHistory(Long patientId, int page, int size, String sortBy);
}
