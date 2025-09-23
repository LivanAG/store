package com.seidor.store.config;


import com.seidor.store.model.User;
import com.seidor.store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    private static UserRepository userRepository;

    // Inicializamos el UserRepository al arrancar Spring
    @Autowired
    public AuthUtil(UserRepository userRepository) {
        AuthUtil.userRepository = userRepository;
    }

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        Object principal = auth.getPrincipal();
        String username;
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
