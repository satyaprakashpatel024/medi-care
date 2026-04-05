package com.care.medi.services;

import com.care.medi.dtos.request.DoctorRequestDTO;
import com.care.medi.dtos.request.DoctorUpdateRequestDTO;
import com.care.medi.dtos.response.DoctorListResponseDTO;
import com.care.medi.dtos.response.DoctorResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DoctorService {
    DoctorResponseDTO createDoctor(DoctorRequestDTO request);
    DoctorResponseDTO getDoctorById(Long id);
    Page<DoctorListResponseDTO> getAllDoctors(int page, int size, String sortBy);
    List<DoctorResponseDTO> getDoctorsByHospital(Long hospitalId);
    List<DoctorResponseDTO> getDoctorsByDepartment(Long departmentId);
    List<DoctorResponseDTO> getDoctorsBySpeciality(String speciality);
    DoctorResponseDTO updateDoctor(Long id, DoctorUpdateRequestDTO request);
    void deleteDoctor(Long id);
}
