package com.care.medi.services;

import com.care.medi.dtos.request.StaffRequestDTO;
import com.care.medi.dtos.request.StaffUpdateRequestDTO;
import com.care.medi.dtos.response.StaffResponseDTO;
import org.springframework.data.domain.Page;

public interface StaffService {
    StaffResponseDTO createStaff(Long hospitalId, StaffRequestDTO request);

    Page<StaffResponseDTO> getAllStaff(int page, int size, String sortBy);

    Page<StaffResponseDTO> getStaffByHospital(Long hospitalId, int page, int size, String sortBy);

    StaffResponseDTO getStaffById(Long id);

    StaffResponseDTO updateStaff(Long id, StaffUpdateRequestDTO request);

    void deleteStaff(Long id);
}
