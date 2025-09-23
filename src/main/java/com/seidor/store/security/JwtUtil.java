package com.seidor.store.security;


//Clase responsable de crear y validar JWT

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    //Recuperamos nuestra clave secreta para firmar y verificar los JWT del archivo application.properties
    @Value("${jwt.secret}")
    private String secret;

    //Recuperamos el tiempo de expiracion de nuestro token del archivo application properties
    @Value("${jwt.expiration-ms}")
    private long expirationMs;


    //Convierte el secret (String) a bytes.
    //Usa Keys.hmacShaKeyFor de JJWT para crear una clave HMAC válida para HS256.
    //Esa clave se usará tanto para firmar como para verificar los tokens.
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now) // fecha de emision
                .setExpiration(expiry) // fecha de expiracion
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)// lo firma con HS256 y tu clave secreta.
                .compact();//devuelve el JWT como string
    }


    //Valida el token
    public boolean validateToken(String token,UserDetails userDetails) {
        //Extrae el username del token.
        final String username = extractUsername(token);

        //Verifica que el username del token coincida con el username del detail service y
        // Verifica que el token no este expirado
        return(username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }



    //Crea un parser con tu clave secreta.
    //Verifica la firma del token.
    //Devuelve las claims (payload) del JWT.
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // clave para verificar firma
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //Comprueba si la fecha de expiración del token es anterior a “ahora”.
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    //Usa extractAllClaims para parsear el token y sacar el “subject” (username o email).
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
}