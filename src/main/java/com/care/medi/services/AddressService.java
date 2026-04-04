package com.care.medi.services;

import com.care.medi.dtos.request.AddressRequestDTO;
import com.care.medi.dtos.response.AddressResponseDTO;
import com.care.medi.entity.Users;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AddressService {

    @Transactional
    AddressResponseDTO createAddress(Users user, AddressRequestDTO request);

    AddressResponseDTO getAddressById(Long id);

    List<AddressResponseDTO> getAddressesByUser(Long userId);

    AddressResponseDTO getDefaultAddressByUser(Long userId);

    @Transactional
    AddressResponseDTO updateAddress(Long id, AddressRequestDTO request);

    @Transactional
    void setDefaultAddress(Long addressId, Long userId);

    @Transactional
    void deleteAddress(Long id);
}
