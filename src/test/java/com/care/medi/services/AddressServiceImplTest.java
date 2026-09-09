package com.care.medi.services;

import com.care.medi.dtos.request.AddressRequestDTO;
import com.care.medi.dtos.response.AddressResponseDTO;
import com.care.medi.entity.Address;
import com.care.medi.entity.AddressType;
import com.care.medi.entity.Users;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.AddressRepository;
import com.care.medi.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService Unit Tests")
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private Users testUser;
    private Address testAddress;
    private AddressRequestDTO addressRequestDTO;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(1L);

        testAddress = Address.builder()
                .user(testUser)
                .phoneNumber("1234567890")
                .addressLine1("123 Main St")
                .city("New York")
                .state("NY")
                .postalCode("10001")
                .country("USA")
                .addressType(AddressType.HOME)
                .isDefault(true)
                .build();
        testAddress.setId(1L);

        addressRequestDTO = new AddressRequestDTO();
        addressRequestDTO.setPhone("1234567890");
        addressRequestDTO.setAddressLine1("123 Main St");
        addressRequestDTO.setCity("New York");
        addressRequestDTO.setState("NY");
        addressRequestDTO.setPostalCode("10001");
        addressRequestDTO.setCountry("USA");
        addressRequestDTO.setAddressType("HOME");
        addressRequestDTO.setIsDefault(true);
    }

    @Test
    @DisplayName("Should create address successfully")
    void testCreateAddress() {
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        AddressResponseDTO response = addressService.createAddress(testUser, addressRequestDTO);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("123 Main St", response.addressLine1());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    @DisplayName("Should return address by id")
    void testGetAddressById() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));

        AddressResponseDTO response = addressService.getAddressById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        verify(addressRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when address not found by id")
    void testGetAddressById_NotFound() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.getAddressById(1L));
        verify(addressRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get addresses by user id")
    void testGetAddressesByUser() {
        when(usersRepository.existsById(1L)).thenReturn(true);
        when(addressRepository.findByUserId(1L)).thenReturn(List.of(testAddress));

        List<AddressResponseDTO> responses = addressService.getAddressesByUser(1L);

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        verify(usersRepository).existsById(1L);
        verify(addressRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Should get default address by user id")
    void testGetDefaultAddressByUser() {
        when(addressRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(Optional.of(testAddress));

        AddressResponseDTO response = addressService.getDefaultAddressByUser(1L);

        assertNotNull(response);
        assertTrue(response.isDefault());
        verify(addressRepository).findByUserIdAndIsDefaultTrue(1L);
    }

    @Test
    @DisplayName("Should update address")
    void testUpdateAddress() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        AddressRequestDTO updateRequest = new AddressRequestDTO();
        updateRequest.setCity("Boston");
        updateRequest.setAddressType("WORK");

        AddressResponseDTO response = addressService.updateAddress(1L, updateRequest);

        assertNotNull(response);
        verify(addressRepository).findById(1L);
        verify(addressRepository).save(any(Address.class));
        assertEquals("Boston", testAddress.getCity());
        assertEquals(AddressType.WORK, testAddress.getAddressType());
    }

    @Test
    @DisplayName("Should set default address")
    void testSetDefaultAddress() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));

        addressService.setDefaultAddress(1L, 1L);

        verify(addressRepository).findById(1L);
        verify(addressRepository).clearDefaultByUserId(1L);
        verify(addressRepository).save(testAddress);
        assertTrue(testAddress.getIsDefault());
    }

    @Test
    @DisplayName("Should delete address")
    void testDeleteAddress() {
        when(addressRepository.existsById(1L)).thenReturn(true);

        addressService.deleteAddress(1L);

        verify(addressRepository).existsById(1L);
        verify(addressRepository).deleteById(1L);
    }
}
