package com.seidor.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seidor.store.dto.auth_dtos.AuthRequestDTO;
import com.seidor.store.dto.auth_dtos.AuthResponseDTO;
import com.seidor.store.dto.auth_dtos.RegisterRequestDTO;
import com.seidor.store.model.Role;
import com.seidor.store.model.User;
import com.seidor.store.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper; // mejor autowired que crear manualmente

    @Test
    void login_shouldReturnToken_WhenCredentialsAreValid() throws Exception {

        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail("user@test.com");
        request.setPassword("password");

        AuthResponseDTO responseDTO = new AuthResponseDTO("token123");
        when(authService.login(any(AuthRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"));

        verify(authService).login(any(AuthRequestDTO.class));
    }

    @Test
    void register_shouldReturnUser_WhenEmailIsAvailable() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("new@test.com");
        request.setPassword("pass123");
        request.setUsername("newuser");
        request.setRole(Role.ADMIN);

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());

        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.com"))
                .andExpect(jsonPath("$.username").value("newuser"));

        verify(authService).register(any(RegisterRequestDTO.class));
    }

    @Test
    void login_shouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {

        AuthRequestDTO request = new AuthRequestDTO();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {

        RegisterRequestDTO request = new RegisterRequestDTO();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
