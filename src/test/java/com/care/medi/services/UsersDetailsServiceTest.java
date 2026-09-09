package com.care.medi.services;

import com.care.medi.entity.Users;
import com.care.medi.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsersDetailsService Unit Tests")
class UsersDetailsServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersDetailsService usersDetailsService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
    }

    @Test
    @DisplayName("Should load user by username successfully")
    void testLoadUserByUsername() {
        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = usersDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        verify(usersRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException for invalid username")
    void testLoadUserByUsername_NotFound() {
        when(usersRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> usersDetailsService.loadUserByUsername("invalid@example.com"));
        verify(usersRepository).findByEmail("invalid@example.com");
    }
}
