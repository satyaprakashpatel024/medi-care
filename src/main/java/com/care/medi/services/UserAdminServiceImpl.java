package com.care.medi.services;

import com.care.medi.dtos.response.UserResponseDTO;
import com.care.medi.entity.Role;
import com.care.medi.entity.Users;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.UsersRepository;
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
public class UserAdminServiceImpl implements UserAdminService {

    private final UsersRepository usersRepository;

    @Override
    public Page<UserResponseDTO> getAllUsers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return usersRepository.findAll(pageable).map(UserResponseDTO::fromEntity);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return UserResponseDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserRole(Long id, Role role) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setRole(role);
        return UserResponseDTO.fromEntity(usersRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserStatus(Long id, Boolean isActive) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setIsActive(isActive);
        return UserResponseDTO.fromEntity(usersRepository.save(user));
    }
}
