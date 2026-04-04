package com.care.medi.services;

import com.care.medi.dtos.request.AddressRequestDTO;
import com.care.medi.dtos.response.AddressResponseDTO;
import com.care.medi.entity.Address;
import com.care.medi.entity.Users;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.AddressRepository;
import com.care.medi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public AddressResponseDTO createAddress(Users user, AddressRequestDTO request) {

        Address address = Address.builder()
                .user(user)
                .phoneNumber(request.getPhoneNumber())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .landmark(request.getLandmark())
                .addressType(request.getAddressType())
                .isDefault(Boolean.TRUE.equals(request.getIsDefault()))
                .build();

        return toResponse(addressRepository.save(address));
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    public AddressResponseDTO getAddressById(Long id) {
        return toResponse(
                addressRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("Address not found for this id : %d", id)))
        );
    }

    @Override
    public List<AddressResponseDTO> getAddressesByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found for userId: " + userId);
        }
        return addressRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponseDTO getDefaultAddressByUser(Long userId) {
        Address address = addressRepository.findByUserIdAndIsDefaultTrue(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("No Default address found for this user id : %d", userId)));
        return toResponse(address);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public AddressResponseDTO updateAddress(Long id, AddressRequestDTO request) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Address not found with id : %d", id)));

        if (request.getPhoneNumber() != null) address.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddressLine1() != null) address.setAddressLine1(request.getAddressLine1());
        if (request.getAddressLine2() != null) address.setAddressLine2(request.getAddressLine2());
        if (request.getCity() != null) address.setCity(request.getCity());
        if (request.getState() != null) address.setState(request.getState());
        if (request.getPostalCode() != null) address.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null) address.setCountry(request.getCountry());
        if (request.getLandmark() != null) address.setLandmark(request.getLandmark());
        if (request.getAddressType() != null) address.setAddressType(request.getAddressType());

        return toResponse(addressRepository.save(address));
    }

    @Transactional
    @Override
    public void setDefaultAddress(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Address not found with id : %d", addressId)));

        // Clear existing default, then mark the requested address as default
        addressRepository.clearDefaultByUserId(userId);
        address.setIsDefault(true);
        addressRepository.save(address);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Address not found with id : %d", id));
        }
        addressRepository.deleteById(id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    AddressResponseDTO toResponse(Address a) {
        return AddressResponseDTO.builder()
                .id(a.getId())
                .userId(a.getUser() != null ? a.getUser().getId() : null)
                .phoneNumber(a.getPhoneNumber())
                .addressLine1(a.getAddressLine1())
                .addressLine2(a.getAddressLine2())
                .city(a.getCity())
                .state(a.getState())
                .postalCode(a.getPostalCode())
                .country(a.getCountry())
                .landmark(a.getLandmark())
                .addressType(a.getAddressType())
                .isDefault(a.getIsDefault())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
