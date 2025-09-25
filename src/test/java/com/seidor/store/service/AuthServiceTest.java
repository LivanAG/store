package com.seidor.store.service;


import com.seidor.store.dto.authDTOS.AuthRequestDTO;
import com.seidor.store.dto.authDTOS.AuthResponseDTO;
import com.seidor.store.dto.authDTOS.RegisterRequestDTO;
import com.seidor.store.model.Role;
import com.seidor.store.model.User;
import com.seidor.store.repository.UserRepository;
import com.seidor.store.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_shouldReturnToken_WhenCredentialsAreCorrect() {

        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail("user@test.com");
        request.setPassword("password");

        UserDetails userDetails = mock(UserDetails.class);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtUtil.generateToken(userDetails)).thenReturn("token123");

        // ACT
        AuthResponseDTO response = authService.login(request);

        // ASSERT
        assertEquals("token123", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(userDetails);
    }

    @Test
    void login_shouldThrowException_WhenAuthenticationFails() {
        // ARRANGE
        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail("user@test.com");
        request.setPassword("wrongpass");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Bad credentials"));

        // ACT + ASSERT
        assertThrows(RuntimeException.class, () -> authService.login(request));
    }


    @Test
    void register_shouldSaveUser_WhenEmailIsNotTaken() {

        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("new@test.com");
        request.setPassword("pass123");
        request.setUsername("newuser");
        request.setRole(Role.CLIENT);
        request.setFirstName("Juan");
        request.setLastName("Perez");
        request.setDni("12345678A");
        request.setPhone(123456789);

        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");

        User savedUser = authService.register(request);


        assertEquals("new@test.com", savedUser.getEmail());
        assertEquals("encodedPass", savedUser.getPassword());
        assertEquals("newuser", savedUser.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_WhenEmailAlreadyExists() {

        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("exist@test.com");

        when(userRepository.findByEmail("exist@test.com"))
                .thenReturn(Optional.of(new User()));

        // ACT + ASSERT
        assertThrows(DataIntegrityViolationException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }
}

