package com.care.medi.services;

import com.care.medi.dtos.request.HospitalAddressRequestDTO;
import com.care.medi.dtos.request.HospitalRequestDTO;
import com.care.medi.dtos.request.HospitalUpdateRequestDTO;
import com.care.medi.dtos.response.HospitalAddressResponseDTO;
import com.care.medi.dtos.response.HospitalDepartmentResponseDTO;
import com.care.medi.dtos.response.HospitalListResponseDTO;
import com.care.medi.dtos.response.HospitalResponseDTO;
import com.care.medi.entity.Department;
import com.care.medi.entity.Hospital;
import com.care.medi.entity.HospitalAddress;
import com.care.medi.entity.HospitalDepartment;
import com.care.medi.exception.BusinessException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.DepartmentRepository;
import com.care.medi.repository.HospitalDepartmentRepository;
import com.care.medi.repository.HospitalRepository;
import com.care.medi.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final HospitalDepartmentRepository hospitalDepartmentRepository;
    private final HospitalAddressServiceImpl hospitalAddressServiceImpl;
    // ── Create ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public HospitalResponseDTO createHospital(HospitalRequestDTO request) {
        Hospital hospital = Hospital.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .build();

        if (request.getAddress() != null) {
            HospitalAddress address = buildHospitalAddress(request.getAddress());
            hospital.addAddress(address);
        }
        return toResponse(hospitalRepository.save(hospital));
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    public HospitalResponseDTO getHospitalById(Long id) {
        Hospital hospital = hospitalRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + id));
        return HospitalResponseDTO.fromEntity(hospital);
    }

    public Page<HospitalListResponseDTO> getAllHospitals(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Hospital> hospitals = hospitalRepository.findAll(pageable);
        return hospitals.map(this::toListResponse);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public HospitalResponseDTO updateHospital(Long id, HospitalUpdateRequestDTO request) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + id));

        if (request.getName() != null) hospital.setName(request.getName());
        if (request.getPhone() != null) hospital.setPhone(request.getPhone());
        if (request.getAddress() != null)
            hospital.addAddress(hospitalAddressServiceImpl.createHospitalAddress(id, request.getAddress()));

        return toResponse(hospitalRepository.save(hospital));
    }

    @Transactional
    public HospitalResponseDTO assignAddressToHospital(Long id, HospitalAddressRequestDTO addressRequest) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + id));
        HospitalAddress hospitalAddress = hospitalAddressServiceImpl.createHospitalAddress(id, addressRequest);
        hospital.addAddress(hospitalAddress);
        return toResponse(hospitalRepository.save(hospital));
    }


    // ── Department Assignment ─────────────────────────────────────────────────

    @Transactional
    @Override
    public void assignDepartment(Long hospitalId, Long departmentId) throws BusinessException {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + hospitalId));
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.DEPARTMENT_NOT_FOUND + departmentId));

        if (hospitalDepartmentRepository.existsByHospitalIdAndDepartmentId(hospitalId, departmentId)) {
            throw new BusinessException("This department is already present in this hospital.");
        }
        hospital.addDepartment(department);
        hospitalRepository.save(hospital);
    }

    @Transactional
    @Override
    public void removeDepartment(Long hospitalId, Long departmentId) throws BusinessException {
        HospitalDepartment hd = hospitalDepartmentRepository
                .findByHospitalIdAndDepartmentId(hospitalId, departmentId)
                .orElseThrow(() -> new BusinessException("Department is not assigned to this hospital"));
        hospitalDepartmentRepository.delete(hd);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public void deleteHospital(Long id) {
        if (!hospitalRepository.existsById(id)) {
            throw new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + id);
        }
        hospitalRepository.deleteById(id);
    }

    public boolean existsById(Long hospitalId) {
        return hospitalRepository.existsById(hospitalId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HospitalAddress buildHospitalAddress(HospitalAddressRequestDTO req) {
        return HospitalAddress.builder()
                .phoneNumber(req.getPhoneNumber())
                .addressLine1(req.getAddressLine1())
                .addressLine2(req.getAddressLine2())
                .city(req.getCity())
                .state(req.getState())
                .postalCode(req.getPostalCode())
                .country(req.getCountry())
                .landmark(req.getLandmark())
                .build();
    }

    private HospitalResponseDTO toResponse(Hospital save) {
        return HospitalResponseDTO.builder()
                .id(save.getId())
                .phone(save.getPhone())
                .name(save.getName())
                .address(toHospitalAddressResponse(save.getAddresses()))
                .departments(toDepartmentResponse(save.getHospitalDepartments()))
                .build();
    }
    private HospitalListResponseDTO toListResponse(Hospital save) {
        return HospitalListResponseDTO.builder()
                .id(save.getId())
                .phone(save.getPhone())
                .name(save.getName())
                .address(toHospitalAddressResponse(save.getAddresses()))
                .build();
    }

    private Set<HospitalDepartmentResponseDTO> toDepartmentResponse(Set<HospitalDepartment> hospitalDepartments) {
        return hospitalDepartments.stream()
                .map(HospitalDepartmentResponseDTO::fromEntity)
                .collect(Collectors.toSet());

    }

    private Set<HospitalAddressResponseDTO> toHospitalAddressResponse(Set<HospitalAddress> addresses) {
        return addresses.stream()
                .map(HospitalAddressResponseDTO::fromEntity)
                .collect(Collectors.toSet());
    }

}
