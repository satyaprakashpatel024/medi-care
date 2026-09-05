package com.care.medi.services;

import com.care.medi.dtos.request.StaffRequestDTO;
import com.care.medi.dtos.request.StaffUpdateRequestDTO;
import com.care.medi.dtos.response.StaffResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.DuplicateResourceException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.HospitalRepository;
import com.care.medi.repository.StaffRepository;
import com.care.medi.repository.UsersRepository;
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
@Transactional(readOnly = true)
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UsersRepository usersRepository;
    private final HospitalRepository hospitalRepository;

    @Override
    @Transactional
    public StaffResponseDTO createStaff(Long hospitalId, StaffRequestDTO request) {
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User already exists with email: " + request.getEmail());
        }

        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + hospitalId));

        Users user = usersRepository.save(Users.toEntity(request.getEmail(), "defaultPass123", Role.STAFF));

        Staff staff = Staff.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender() != null ? Gender.valueOf(request.getGender()) : null)
                .phone(request.getPhone())
                .hospital(hospital)
                .emergencyContact(request.getEmergencyContact())
                .bloodGroup(request.getBloodGroup() != null ? BloodGroup.valueOf(request.getBloodGroup()) : null)
                .build();

        Staff savedStaff = staffRepository.save(staff);
        return mapToResponse(savedStaff);
    }

    @Override
    public Page<StaffResponseDTO> getAllStaff(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return staffRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public Page<StaffResponseDTO> getStaffByHospital(Long hospitalId, int page, int size, String sortBy) {
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + hospitalId);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return staffRepository.findByHospitalId(hospitalId, pageable).map(this::mapToResponse);
    }

    @Override
    public StaffResponseDTO getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with ID: " + id));
        return mapToResponse(staff);
    }

    @Override
    @Transactional
    public StaffResponseDTO updateStaff(Long id, StaffUpdateRequestDTO request) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with ID: " + id));

        if (request.getFirstName() != null) staff.setFirstName(request.getFirstName());
        if (request.getLastName() != null) staff.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null) staff.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) staff.setGender(Gender.valueOf(request.getGender()));
        if (request.getPhone() != null) staff.setPhone(request.getPhone());
        if (request.getEmergencyContact() != null) staff.setEmergencyContact(request.getEmergencyContact());
        if (request.getBloodGroup() != null) staff.setBloodGroup(BloodGroup.valueOf(request.getBloodGroup()));

        return mapToResponse(staffRepository.save(staff));
    }

    @Override
    @Transactional
    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with ID: " + id));

        staffRepository.delete(staff);
        usersRepository.delete(staff.getUser());
    }

    private StaffResponseDTO mapToResponse(Staff staff) {
        return StaffResponseDTO.builder()
                .id(staff.getId())
                .userId(staff.getUser() != null ? staff.getUser().getId() : null)
                .firstName(staff.getFirstName())
                .lastName(staff.getLastName())
                .dateOfBirth(staff.getDateOfBirth())
                .gender(staff.getGender() != null ? staff.getGender().name() : null)
                .phone(staff.getPhone())
                .hospitalId(staff.getHospital() != null ? staff.getHospital().getId() : null)
                .hospitalName(staff.getHospital() != null ? staff.getHospital().getName() : null)
                .emergencyContact(staff.getEmergencyContact())
                .bloodType(staff.getBloodGroup() != null ? staff.getBloodGroup().name() : null)
                .updatedAt(staff.getUpdatedAt())
                .build();
    }
}
