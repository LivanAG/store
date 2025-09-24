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

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                // Token expirado → devuelve 401 con mensaje
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token expirado\"}");
                return; // detener el filtro, no seguimos con la cadena
            } catch (Exception e) {
                // Token inválido o mal formado
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token inválido\"}");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = myUserDetailService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
