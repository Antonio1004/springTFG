package com.marketplace.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestFilter extends OncePerRequestFilter {

	private static final String[] WHITELIST = {
	        "/users/registrar",
	        "/users/login",
	        "/swagger-ui/",
	        "/v3/api-docs/"
	    };
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String path = request.getRequestURI();

        // Si la ruta está en la whitelist, no intentamos validar token
        for (String free : WHITELIST) {
            if (path.startsWith(free)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        
		String token = request.getHeader("Authorization");
		
		if (token != null) {
			
			try {
				UsernamePasswordAuthenticationToken authentication = TokenUtils.decodeToken(token.substring(7));
				//authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			
			} catch (ExpiredJwtException e) {
				throw new RuntimeException("Token expirado");
			
			} catch (MalformedJwtException e) {
				throw new MalformedJwtException("Token mal formado");
			
			} catch (Exception e) {
				throw new RuntimeException("Error en autenticación: " + e.getMessage(), e);
			}

		}
		
		filterChain.doFilter(request, response);

	}
}
