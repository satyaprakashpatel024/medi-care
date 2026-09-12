package com.care.medi.services;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.care.medi.dtos.response.UserResponseDTO;
import com.care.medi.entity.Role;
import com.care.medi.entity.Users;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAdminService Unit Tests")
class UserAdminServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserAdminServiceImpl userAdminService;

    private Users testUser;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(1L);
        testUser.setEmail("admin@test.com");
        testUser.setRole(Role.SUPER_ADMIN);
        testUser.setIsActive(true);
    }

    @Test
    @DisplayName("Should get all users with pagination")
    void testGetAllUsers() {
        Page<Users> page = new PageImpl<>(List.of(testUser));
        when(usersRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<UserResponseDTO> response = userAdminService.getAllUsers(0, 10, "id");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("admin@test.com", response.getContent().get(0).email());
        verify(usersRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should get user by ID")
    void testGetUserById() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponseDTO response = userAdminService.getUserById(1L);

        assertNotNull(response);
        assertEquals("admin@test.com", response.email());
        verify(usersRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid ID")
    void testGetUserById_NotFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userAdminService.getUserById(1L));
        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Should update user role")
    void testUpdateUserRole() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(usersRepository.save(any(Users.class))).thenReturn(testUser);

        UserResponseDTO response = userAdminService.updateUserRole(1L, Role.DOCTOR);

        assertNotNull(response);
        assertEquals(Role.DOCTOR, response.role());
        verify(usersRepository).findById(1L);
        verify(usersRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating role for invalid ID")
    void testUpdateUserRole_NotFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userAdminService.updateUserRole(1L, Role.DOCTOR));
        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Should update user status")
    void testUpdateUserStatus() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(usersRepository.save(any(Users.class))).thenReturn(testUser);

        UserResponseDTO response = userAdminService.updateUserStatus(1L, false);

        assertNotNull(response);
        assertFalse(response.isActive());
        verify(usersRepository).findById(1L);
        verify(usersRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating status for invalid ID")
    void testUpdateUserStatus_NotFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userAdminService.updateUserStatus(1L, false));
        assertEquals("User not found with ID: 1", exception.getMessage());
    }
}
