package com.care.medi.services;

import com.care.medi.dtos.request.DoctorRequestDTO;
import com.care.medi.dtos.request.DoctorUpdateRequestDTO;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.DoctorListResponseDTO;
import com.care.medi.dtos.response.DoctorResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface DoctorService {
    @Transactional
    DoctorResponseDTO createDoctor(DoctorRequestDTO request);

    DoctorResponseDTO getDoctorById(Long id);

    Page<DoctorListResponseDTO> getAllActiveDoctors(int page, int size, String sortBy);

    Page<DoctorListResponseDTO> getActiveDoctorsByHospital(Long hospitalId, int page, int size, String sortBy);

    Page<DoctorListResponseDTO> getActiveDoctorsByDepartmentAndHospital(Long departmentId, Long hospitalId, int page, int size, String sortBy);

    Page<DoctorListResponseDTO> getActiveDoctorsBySpeciality(String speciality, int page, int size, String sortBy);

    @Transactional
    DoctorResponseDTO updateDoctor(Long id, DoctorUpdateRequestDTO request);

    @Transactional
    void deleteDoctor(Long id);

    Page<AppointmentListResponseDTO> getAppointmentsByDoctorAndDate(Long id, Integer page, Integer size, String sortBy, LocalDate date);
}
