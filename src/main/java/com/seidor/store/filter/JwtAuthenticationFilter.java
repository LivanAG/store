package com.seidor.store.filter;


import com.seidor.store.security.JwtUtil;
import com.seidor.store.service.MyUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


//Este filtro intercepta cada petición http para:
//extraer el token de la cabecera Authorization
//validarlo, si es valido
//Cargar el usuario
//Meterlo en el SecurityContext para que el resto de la app sepa que está autenticado.


//OncePerRequestFilter: Es una clase base de Spring Security que garantiza que tu filtro se ejecute una sola vez por request.
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    //Inyectas JwtUtil (para manipular tokens) y MyUserDetailsService (para cargar usuarios de BD).
    private final JwtUtil jwtUtil;
    private final MyUserDetailService myUserDetailService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, MyUserDetailService myUserDetailService) {
        this.jwtUtil = jwtUtil;
        this.myUserDetailService = myUserDetailService;
    }


    //El metodo doFilterInternal es el corazón del filtro.
    // Se ejecuta en cada request antes de que Spring Security decida quién es el usuario.


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        //Busca la cabecera Authorization.
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;


        //Comprueba que empiece con "Bearer " (el estándar para JWT).
        //Extrae el token (quita los 7 caracteres de "Bearer ").
        //Intenta leer el username (o email) con jwtUtil.extractUsername(jwt)
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);// quita "Bearer "
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (IllegalArgumentException e) {
                // token inválido o mal formado -> no autenticamos
            }
        }

        //Si tenemos username y no hay autenticación previa
        //SecurityContextHolder es donde Spring Security guarda al usuario autenticado.
        //Si aún no hay usuario en el contexto, procedemos a autenticar.
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //Cargar usuario y validar token


            //Carga de BD al usuario con userDetailsService (para comparar roles y password si hace falta).
            UserDetails userDetails = myUserDetailService.loadUserByUsername(username);

            //Comprueba que el token es válido con jwtUtil.validateToken(jwt, userDetails) (firma + expiración + username).
            if(jwtUtil.validateToken(jwt, userDetails)) {

                //Crea un UsernamePasswordAuthenticationToken (es la representación de un usuario autenticado en Spring).
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }




        //Deja pasar la request
        filterChain.doFilter(request, response);
    }
}
