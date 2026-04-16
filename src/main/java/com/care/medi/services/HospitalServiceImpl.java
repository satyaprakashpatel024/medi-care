package com.care.medi.services;

import com.care.medi.dtos.request.HospitalAddressRequestDTO;
import com.care.medi.dtos.request.HospitalRequestDTO;
import com.care.medi.dtos.request.HospitalUpdateRequestDTO;
import com.care.medi.dtos.response.HospitalAddressResponseDTO;
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
        return toResponse(
                hospitalRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("Hospital not found with id : %d", id)))
        );
    }

    public Page<HospitalResponseDTO> getAllHospitals(int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Hospital> hospitals = hospitalRepository.findAll(pageable);

        List<HospitalResponseDTO> responseList = hospitals.stream()
                .map(this::toResponse)
                .toList();

        return new PageImpl<>(
                responseList,
                pageable,
                hospitals.getTotalElements()
        );
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public HospitalResponseDTO updateHospital(Long id, HospitalUpdateRequestDTO request) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Hospital with id %d not found", id)));

        if (request.getName() != null) hospital.setName(request.getName());
        if (request.getPhone() != null) hospital.setPhone(request.getPhone());
        if (request.getAddress() != null)
            hospital.addAddress(hospitalAddressServiceImpl.createHospitalAddress(id, request.getAddress()));

        return toResponse(hospitalRepository.save(hospital));
    }

    @Transactional
    public HospitalResponseDTO assignAddressToHospital(Long id, HospitalAddressRequestDTO addressRequest) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Hospital with id %d not found", id)));
        HospitalAddress hospitalAddress = hospitalAddressServiceImpl.createHospitalAddress(id, addressRequest);
        hospital.addAddress(hospitalAddress);
        return toResponse(hospitalRepository.save(hospital));
    }


    // ── Department Assignment ─────────────────────────────────────────────────

    @Transactional
    @Override
    public void assignDepartment(Long hospitalId, Long departmentId) throws BusinessException {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Hospital with id %d not found", hospitalId)));
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Department with id %d not found", departmentId)));

        if (hospitalDepartmentRepository.existsByHospitalIdAndDepartmentId(hospitalId, departmentId)) {
            throw new BusinessException("Department already assigned to this hospital");
        }
        hospital.addDepartment(department);
        hospitalRepository.save(hospital);
    }

    @Transactional
    @Override
    public void removeDepartment(Long hospitalId, Long departmentId) throws BusinessException {
        HospitalDepartment hd = hospitalDepartmentRepository
                .findByHospitalIdAndDepartmentId(hospitalId, departmentId)
                .orElseThrow(() -> new BusinessException(
                        "Department is not assigned to this hospital"));
        hospitalDepartmentRepository.delete(hd);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public void deleteHospital(Long id) {
        if (!hospitalRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Hospital with id %d not found", id));
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
                .departmentNames(toDepartmentResponse(save.getHospitalDepartments()))
                .build();
    }

    private Set<String> toDepartmentResponse(Set<HospitalDepartment> hospitalDepartments) {
        return hospitalDepartments.stream()
                .map(hd -> hd.getDepartment().getName())
                .collect(Collectors.toSet());
    }

    private Set<HospitalAddressResponseDTO> toHospitalAddressResponse(Set<HospitalAddress> addresses) {
        return addresses.stream()
                .map(hospitalAddressServiceImpl::toHospitalAddressResponse)
                .collect(Collectors.toSet());
    }


}
