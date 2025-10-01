package com.seidor.store.controller;


import com.seidor.store.dto.auth_dtos.AuthRequestDTO;
import com.seidor.store.dto.auth_dtos.AuthResponseDTO;
import com.seidor.store.dto.auth_dtos.RegisterRequestDTO;
import com.seidor.store.dto.auth_dtos.RegisterResponseDTO;
import com.seidor.store.model.User;
import com.seidor.store.service.AuthService;
import com.seidor.store.utils.loggers.AppLogger;
import com.seidor.store.utils.loggers.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.ResponseEntity;
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

        AppLogger logger = LoggerFactory.getLogger(null, "/auth");

        ThreadContext.put("usuario", request.getEmail());

        logger.logInfo("Intento de login de " + request.getEmail());

        AuthResponseDTO response = authService.login(request);

        logger.logInfo("Logueo exitoso de " + request.getEmail());

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

        AppLogger logger = LoggerFactory.getLogger(null, "/auth");

        AuthResponseDTO response = authService.refreshToken(request);

        logger.logInfo("JWT renovado para " + response.getToken());

        return ResponseEntity.ok(response);
    }
}
