package com.care.medi.services;

import com.care.medi.dtos.response.UserResponseDTO;
import com.care.medi.entity.Role;
import org.springframework.data.domain.Page;

public interface UserAdminService {
    Page<UserResponseDTO> getAllUsers(int page, int size, String sortBy);

    UserResponseDTO getUserById(Long id);

    UserResponseDTO updateUserRole(Long id, Role role);

    UserResponseDTO updateUserStatus(Long id, Boolean isActive);
}
