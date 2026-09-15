package com.care.medi.services;

import com.care.medi.dtos.request.DoctorScheduleRequestDTO;
import com.care.medi.dtos.response.DoctorScheduleResponseDTO;
import com.care.medi.entity.DoctorSchedule;

public interface DoctorScheduleService {

    DoctorScheduleResponseDTO createOrUpdateSchedule(Long hospitalId, Long doctorId, DoctorScheduleRequestDTO request);

    DoctorScheduleResponseDTO getScheduleByDoctorAndHospital(Long hospitalId, Long doctorId);

    DoctorSchedule getDoctorScheduleEntityOrDefault(Long hospitalId, Long doctorId);
}
