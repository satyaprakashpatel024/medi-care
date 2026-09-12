package com.care.medi.controller;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.care.medi.dtos.request.UserRoleUpdateRequestDTO;
import com.care.medi.dtos.request.UserStatusUpdateRequestDTO;
import com.care.medi.dtos.response.UserResponseDTO;
import com.care.medi.entity.Role;
import com.care.medi.security.JwtAuthenticationFilter;
import com.care.medi.security.JwtService;
import com.care.medi.services.UserAdminService;
import com.care.medi.services.UsersDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserAdminService userAdminService;

    @MockitoBean
    @SuppressWarnings("unused")
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    @SuppressWarnings("unused")
    private JwtService jwtService;

    @MockitoBean
    @SuppressWarnings("unused")
    private UsersDetailsService usersDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponseDTO userResponseDTO;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        userResponseDTO = UserResponseDTO.builder()
                .id(1L)
                .email("admin@care.com")
                .role(Role.SUPER_ADMIN)
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Should get all users paginated")
    void testGetAllUsers() throws Exception {
        Page<UserResponseDTO> page = new PageImpl<>(Collections.singletonList(userResponseDTO));
        when(userAdminService.getAllUsers(anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Users retrieved successfully"))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].email").value("admin@care.com"));
    }

    @Test
    @DisplayName("Should get user by ID")
    void testGetUserById() throws Exception {
        when(userAdminService.getUserById(1L)).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/v1/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should update user role")
    void testUpdateUserRole() throws Exception {
        UserRoleUpdateRequestDTO request = UserRoleUpdateRequestDTO.builder()
                .role(Role.SUPER_ADMIN)
                .build();

        when(userAdminService.updateUserRole(eq(1L), eq(Role.SUPER_ADMIN))).thenReturn(userResponseDTO);

        mockMvc.perform(put("/api/v1/admin/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("User role updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should update user status")
    void testUpdateUserStatus() throws Exception {
        UserStatusUpdateRequestDTO request = UserStatusUpdateRequestDTO.builder()
                .isActive(true)
                .build();

        when(userAdminService.updateUserStatus(eq(1L), eq(true))).thenReturn(userResponseDTO);

        mockMvc.perform(patch("/api/v1/admin/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("User status updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
