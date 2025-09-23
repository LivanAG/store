package com.seidor.store.controller;


import com.seidor.store.dto.authDTOS.AuthRequestDTO;
import com.seidor.store.dto.authDTOS.AuthResponseDTO;
import com.seidor.store.dto.authDTOS.RegisterRequestDTO;
import com.seidor.store.dto.authDTOS.RegisterResponseDTO;
import com.seidor.store.model.User;
import com.seidor.store.security.JwtUtil;
import com.seidor.store.service.MyUserDetailService;
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

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MyUserDetailService userDetailsService;


    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil, MyUserDetailService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),
                        request.getPassword())
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO request)
    {
        User newUser = userDetailsService.register(request);
        RegisterResponseDTO registerResponse = new RegisterResponseDTO(newUser);
        return ResponseEntity.ok(registerResponse);
    }
}
