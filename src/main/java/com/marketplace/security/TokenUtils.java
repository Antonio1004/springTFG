package com.marketplace.security;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;

public class TokenUtils {
	//CREAMOS LA SEMILLA
	private final static String ACCESS_TOKEN_SECRET="transferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJaca";
	
	//TIEMPO EN milisegundos 4 MINUTOS
	// SOLO RECOMENDABLE EN UN PRINCIPIO MIENTRAS DESARROLLAMOS
	private final static Long ACCESS_TOKEN_LIFE_TIME = (long) (60*4*1000);
	
	public static String generateToken(String username, String role ,String foto ,String name,Long id) {
        Date expirationDate = new Date(System.currentTimeMillis() + ACCESS_TOKEN_LIFE_TIME);
        Map<String, Object> payload = new HashMap<>();

        // Añadimos el prefijo ROLE_ al rol
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        payload.put("role", roleWithPrefix);
        payload.put("name", name);         // nombre del usuario
        payload.put("foto", foto);
        System.out.println("ID que se está pasando al token: " + id);

        payload.put("idUser", id);

        payload.put("username", username);

        String token = Jwts.builder()
                .subject(username)
                .issuedAt(expirationDate)
                .claims(payload)
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
                .compact();
        return "Bearer " + token;
    }
	
//	public static UsernamePasswordAuthenticationToken decodeToken(String token) {
//		if (token.startsWith("Bearer ")) {
//			throw new MalformedJwtException("Formato no encontrado");
//		}
//		token.substring(7);
//		Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
//				.build().parseSignedClaims(token)
//				.getPayload();
//		String username = claims.getSubject(); //claims.get("user")
//		String role = (String) claims.get("role");
//		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//		authorities.add(new SimpleGrantedAuthority(role));
//		return new UsernamePasswordAuthenticationToken(username, null, authorities);
//	}
	
	public static UsernamePasswordAuthenticationToken decodeToken(String token) {
        if (!token.startsWith("Bearer ")) {
            throw new MalformedJwtException("Formato no válido, falta 'Bearer '");
        }

        token = token.substring(7);

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.getSubject();
        String role = (String) claims.get("role");

        // Aseguramos el prefijo ROLE_ al rol
        if (role == null) {
            throw new MalformedJwtException("El token no contiene el rol");
        }
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

	
	
}