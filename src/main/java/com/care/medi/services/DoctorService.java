package com.care.medi.services;

import com.care.medi.dtos.request.DoctorRequestDTO;
import com.care.medi.dtos.request.DoctorUpdateRequestDTO;
import com.care.medi.dtos.response.DoctorResponseDTO;

import java.util.List;

public interface DoctorService {
    DoctorResponseDTO createDoctor(DoctorRequestDTO request);
    DoctorResponseDTO getDoctorById(Long id);
    List<DoctorResponseDTO> getAllDoctors();
    List<DoctorResponseDTO> getDoctorsByHospital(Long hospitalId);
    List<DoctorResponseDTO> getDoctorsByDepartment(Long departmentId);
    List<DoctorResponseDTO> getDoctorsBySpeciality(String speciality);
    DoctorResponseDTO updateDoctor(Long id, DoctorUpdateRequestDTO request);
    void deleteDoctor(Long id);
}
