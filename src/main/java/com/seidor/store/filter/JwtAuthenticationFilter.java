package com.seidor.store.filter;

import com.seidor.store.security.JwtUtil;
import com.seidor.store.service.MyUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
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


public class JwtAuthenticationFilter extends OncePerRequestFilter {

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
        String username = "desconocido"; // valor por defecto
        String jwt = null;

        // Extraer token JWT
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            String usuario = ""; // Valor por defecto para el log
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                // Token expirado → devuelve 401 con mensaje y log
                logTokenError("Token expirado", e, usuario, response);
                return; // detener el filtro
            } catch (Exception e) {
                // Token inválido o mal formado
                logTokenError("Token inválido", e, usuario, response);
                return;
            }
        }

        // Autenticar usuario en SecurityContext si hay token válido
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null && jwt != null) {
            UserDetails userDetails = myUserDetailService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Meter usuario en ThreadContext para logs
        ThreadContext.put("usuario", username);

        try {
            // Continuar con la cadena de filtros / controlador
            filterChain.doFilter(request, response);
        } finally {
            // Limpiar siempre el ThreadContext al final
            ThreadContext.clearAll();
        }
    }

    private void logTokenError(String mensaje, Exception ex, String usuario, HttpServletResponse response) throws IOException {
        Logger logger = LogManager.getLogger("authLogger");

        // Guardar usuario en ThreadContext para logs
        ThreadContext.put("usuario", usuario);

        // Log del error
        logger.error("{} - {}", mensaje, ex.getMessage());

        // Respuesta JSON
        String json = String.format(
                "{\"httpStatus\": %d, \"message\": \"%s\", \"code\": \"%s\", \"backendMessage\": \"%s\"}",
                HttpServletResponse.SC_UNAUTHORIZED,
                mensaje,
                "AUTH_ERROR",
                ex.getMessage()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(json);

        ThreadContext.clearAll();
    }
}
