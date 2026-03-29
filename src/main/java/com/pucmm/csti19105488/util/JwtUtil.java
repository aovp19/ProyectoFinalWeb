package com.pucmm.csti19105488.util;

import com.pucmm.csti19105488.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class JwtUtil {

    public static final String SECRET = "proyecto_final_web_icc352_pucmm_2026";

    // Se genera el token a partir del usuario
    public static String generarToken(Usuario usuario) {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());

        // Token valido por 1 hora
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(1);
        Date fechaExpiracion = Date.from(localDateTime.toInstant(ZoneOffset.ofHours(-4)));

        return Jwts.builder()
                .issuer("PUCMM-EICT")
                .subject("ProyectoFinalWeb")
                .expiration(fechaExpiracion)
                .claim("email", usuario.getEmail())
                .claim("rol", usuario.getRol())
                .signWith(secretKey)
                .compact();
    }

    // Verifica y retorna los claims del token
    public static Claims verificarToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseSignedClaims(token.trim())
                .getPayload();
    }
}
