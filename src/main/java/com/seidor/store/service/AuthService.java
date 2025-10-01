package com.seidor.store.service;

import com.seidor.store.dto.auth_dtos.AuthRequestDTO;
import com.seidor.store.dto.auth_dtos.AuthResponseDTO;
import com.seidor.store.dto.auth_dtos.RegisterRequestDTO;
import com.seidor.store.exception.my_exceptions.ResourceNotFoundException;
import com.seidor.store.model.User;
import com.seidor.store.repository.UserRepository;
import com.seidor.store.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    private final MyUserDetailService myUserDetailService;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       MyUserDetailService myUserDetailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.myUserDetailService = myUserDetailService;
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

    public AuthResponseDTO refreshToken(HttpServletRequest request) {

        String oldToken = request.getHeader("Authorization");
        if (oldToken == null || !oldToken.startsWith("Bearer ")) {
            throw new ResourceNotFoundException("Token no proporcionado");
        }

        oldToken = oldToken.substring(7);

        String username;
        try {
            username = jwtUtil.extractUsername(oldToken);
        } catch (Exception e) {
            throw new RuntimeException("Token inválido o expirado");
        }

        UserDetails userDetails = myUserDetailService.loadUserByUsername(username);

        String newToken = jwtUtil.generateToken(userDetails);

        return new AuthResponseDTO(newToken);
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
