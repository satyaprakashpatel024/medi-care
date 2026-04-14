package com.care.medi.services;

import com.care.medi.dtos.request.DoctorRequestDTO;
import com.care.medi.dtos.request.DoctorUpdateRequestDTO;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.DoctorListResponseDTO;
import com.care.medi.dtos.response.DoctorResponseDTO;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface DoctorService {
    @SneakyThrows
    @Transactional
    DoctorResponseDTO createDoctorInHospital(Long hospitalId, DoctorRequestDTO request);

    Page<DoctorListResponseDTO> getActiveDoctorsByDepartmentAndHospital(Long departmentId, Long hospitalId, int page, int size, String sortBy);

    Page<DoctorListResponseDTO> getActiveDoctorsBySpecialityAndHospital(String speciality, Long hospitalId, int page, int size, String sortBy);

    DoctorResponseDTO getDoctorByIdAndHospital(Long id, Long hospitalId);

    Page<DoctorListResponseDTO> getAllActiveDoctorsByHospital(Long hospitalId, int page, int size, String sortBy);

    Page<DoctorListResponseDTO> getAllActiveDoctors(int page, int size, String sortBy);

    Page<AppointmentListResponseDTO> getAppointmentsByDoctorAndHospitalAndDate(Long id, Long hospitalId, LocalDate date, Integer page, Integer size, String sortBy);

    @Transactional
    DoctorResponseDTO updateDoctorByIdAndHospital(Long id, Long hospitalId, DoctorUpdateRequestDTO request);

    @Transactional
    void deleteDoctorByIdAndHospital(Long id, Long hospitalId);
}
