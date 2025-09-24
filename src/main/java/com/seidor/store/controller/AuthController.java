package com.seidor.store.controller;


import com.seidor.store.dto.authDTOS.AuthRequestDTO;
import com.seidor.store.dto.authDTOS.AuthResponseDTO;
import com.seidor.store.dto.authDTOS.RegisterRequestDTO;
import com.seidor.store.dto.authDTOS.RegisterResponseDTO;
import com.seidor.store.model.User;
import com.seidor.store.security.JwtUtil;
import com.seidor.store.service.AuthService;
import com.seidor.store.service.MyUserDetailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;
    public AuthController( AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request)
    {
        User newUser = authService.register(request);
        RegisterResponseDTO registerResponse = new RegisterResponseDTO(newUser);
        return ResponseEntity.ok(registerResponse);
    }
}
