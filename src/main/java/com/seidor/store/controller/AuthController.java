package com.seidor.store.controller;


import com.seidor.store.dto.authDTOS.AuthRequestDTO;
import com.seidor.store.dto.authDTOS.AuthResponseDTO;
import com.seidor.store.dto.authDTOS.RegisterRequestDTO;
import com.seidor.store.dto.authDTOS.RegisterResponseDTO;
import com.seidor.store.model.User;
import com.seidor.store.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController( AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {

        Logger logger = LogManager.getLogger("authLogger");

        ThreadContext.put("usuario", request.getEmail());
        logger.info("Intento de login de {}", request.getEmail());

        AuthResponseDTO response = authService.login(request);

        logger.debug("Logueo exitoso");
        ThreadContext.clearAll();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request)
    {
        User newUser = authService.register(request);
        RegisterResponseDTO registerResponse = new RegisterResponseDTO(newUser);
        return ResponseEntity.ok(registerResponse);
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(HttpServletRequest request) {

        Logger logger = LogManager.getLogger("authLogger");

        AuthResponseDTO response = authService.refreshToken(request);

        logger.info("JWT renovado para {}", response.getToken());
        ThreadContext.clearAll();
        return ResponseEntity.ok(response);
    }
}
