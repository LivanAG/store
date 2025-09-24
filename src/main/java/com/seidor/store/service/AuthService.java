package com.seidor.store.service;

import com.seidor.store.dto.authDTOS.AuthRequestDTO;
import com.seidor.store.dto.authDTOS.AuthResponseDTO;
import com.seidor.store.dto.authDTOS.RegisterRequestDTO;
import com.seidor.store.exception.myExceptions.ResourceNotFoundException;
import com.seidor.store.model.User;
import com.seidor.store.repository.UserRepository;
import com.seidor.store.security.JwtUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponseDTO login(AuthRequestDTO authRequest) {

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(),
                            authRequest.getPassword())
            );
            UserDetails userDetails = (UserDetails) auth.getPrincipal();

            String token = jwtUtil.generateToken(userDetails);
            return new AuthResponseDTO(token);


    }

    public User register(RegisterRequestDTO request){


        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException("Ya existe ese email, trata de iniciar sesion");
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(request.getUsername());
        user.setRole(request.getRole());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDni(request.getDni());
        user.setPhone(request.getPhone());

        userRepository.save(user);
        return user;


    }
}
