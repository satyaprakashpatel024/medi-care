package com.care.medi.services;

import com.care.medi.dtos.response.PrescriptionResponseDTO;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.PrescriptionRepository;
import com.care.medi.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl {
    private final PrescriptionRepository prescriptionRepository;
    private final PatientService patientService;
    @Transactional(readOnly = true)
    public Page<PrescriptionResponseDTO> getPrescriptionByPatientId(Long hospitalId,Long patientId, int page, int size, String sortBy) {
        if(!patientService.existsByIdAndHospitalId(patientId,hospitalId)){
            throw new ResourceNotFoundException(String.format(Constants.PATIENT_NOT_FOUND_IN_HOSPITAL, patientId));
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return prescriptionRepository.findByPatientId(patientId,pageable);
    }


}
