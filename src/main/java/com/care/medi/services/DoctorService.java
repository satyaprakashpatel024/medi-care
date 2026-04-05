package com.care.medi.services;

import com.care.medi.dtos.request.DoctorRequestDTO;
import com.care.medi.dtos.request.DoctorUpdateRequestDTO;
import com.care.medi.dtos.response.DoctorListResponseDTO;
import com.care.medi.dtos.response.DoctorResponseDTO;
import org.springframework.data.domain.Page;

public interface DoctorService {
    DoctorResponseDTO createDoctor(DoctorRequestDTO request);

    DoctorResponseDTO getDoctorById(Long id);

    Page<DoctorListResponseDTO> getAllDoctors(int page, int size, String sortBy);

    Page<DoctorListResponseDTO> getDoctorsByHospital(Long hospitalId, int page, int size, String sortBy);

    Page<DoctorListResponseDTO> getDoctorsByDepartmentAndHospital(Long departmentId,Long hospitalId, int page, int size, String sortBy);

    Page<DoctorListResponseDTO> getDoctorsBySpeciality(String speciality, int page, int size, String sortBy);

    DoctorResponseDTO updateDoctor(Long id, DoctorUpdateRequestDTO request);

    void deleteDoctor(Long id);
}
