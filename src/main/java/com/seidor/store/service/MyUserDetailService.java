package com.seidor.store.service;


//import com.seidor.store.dto.RegisterRequest;

import com.seidor.store.dto.authDTOS.RegisterRequestDTO;
import com.seidor.store.model.User;
import com.seidor.store.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //Va al repositorio y busca en la base de datos al User (tu entidad) cuyo campo email coincide con el que Spring Security pasó.
        //Si no encuentra nada, lanza UsernameNotFoundException (Spring Security la intercepta y la traduce a “usuario o contraseña inválidos”).
        User user =  userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Usuario no encontrado"));


        // Construir la lista de authorities a partir del enum
        // Spring Security espera los roles con el prefijo "ROLE_"
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );


        //Spring Security no trabaja con tu entidad User directamente, sino con su propia interfaz UserDetails.
        //Por eso aquí creas un org.springframework.security.core.userdetails.User, que es una implementación de UserDetails.
        //Le pasas:
        //el username del usuario,
        //la contraseña encriptada (que tiene que estar en la BD en formato hash, nunca en texto plano),
        //y la lista de roles/permisos (authorities).

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);

    }



}
