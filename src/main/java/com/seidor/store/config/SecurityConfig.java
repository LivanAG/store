package com.seidor.store.config;


import com.seidor.store.filter.JwtAuthenticationFilter;
import com.seidor.store.security.JwtUtil;
import com.seidor.store.service.MyUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MyUserDetailService myUserDetailService;
    private final JwtUtil jwtUtil;

    public SecurityConfig(MyUserDetailService myUserDetailService, JwtUtil jwtUtil) {
        this.myUserDetailService = myUserDetailService;
        this.jwtUtil = jwtUtil;
    }



    //AuthenticationManager es el núcleo que autentica usuarios (normalmente usando tu UserDetailsService + PasswordEncoder).
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtil, myUserDetailService);

        http
                // CSRF (Cross-Site Request Forgery) protege forms y cookies en aplicaciones web tradicionales.
                //En APIs REST stateless (sin sesiones ni cookies, usando JWT) no tiene sentido mantenerlo, por eso se desactiva.
                //Objetivo: evitar que Spring Security bloquee tus peticiones POST/PUT/DELETE con tokens JWT, ya que no dependemos de sesión ni cookies.
                .csrf(csrf -> csrf.disable())


                // SessionCreationPolicy.STATELESS le dice a Spring Security:
                //No crear sesión HTTP.
                //No guardar usuario en memoria entre requests.
                //Objetivo: cada request debe autenticarse independientemente mediante JWT
                //Esto es clave para APIs REST modernas, ya que queremos escalabilidad y no depender de sesiones del servidor.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )


                .authorizeHttpRequests(authz ->
                        authz
                                // Rutas públicas
                                .requestMatchers("/auth/**").permitAll()

                                // GET /product/** -> ADMIN o CLIENT
                                .requestMatchers(HttpMethod.GET, "/product/**").hasAnyRole("ADMIN", "CLIENT")

                                // POST, PUT, DELETE /product/** -> solo ADMIN
                                .requestMatchers(HttpMethod.POST, "/product/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/product/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/product/**").hasRole("ADMIN")

                                // Todas las demás rutas requieren autenticación
                                .anyRequest().authenticated()
                )

                // addFilterBefore inserta tu JwtAuthenticationFilter antes del filtro estándar de Spring Security
                // que maneja login con formulario (UsernamePasswordAuthenticationFilter).
                //Objetivo: que antes de procesar cualquier request, el filtro JWT:
                //Extraiga y valide el token.
                //Autentique al usuario si el token es válido.
                //Esto garantiza que los endpoints protegidos tengan SecurityContext ya configurado.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // Spring Security por defecto bloquea mostrar páginas en <iframe> por seguridad.
                //La consola de H2 usa <iframe>, así que desactivamos esa restricción solo para poder usarla.
                //Objetivo: permitir abrir la H2 Console en el navegador sin problemas de seguridad del frame.
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}