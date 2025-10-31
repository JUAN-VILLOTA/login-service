package com.couriersync.login.login_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Skip JWT filter for Swagger and public endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/swagger-ui") || 
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/api/login") ||
            path.startsWith("/actuator/health")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Extraer el token del header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("Error al extraer username del token: " + e.getMessage());
            }
        }

        // Validar y autenticar
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt, username)) {
                // Extraer rol y permisos
                String role = jwtUtil.extractRole(jwt);
                List<String> permisos = jwtUtil.extractPermisos(jwt);

                // Crear las authorities (rol + permisos)
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (permisos != null) {
                    authorities = permisos.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                }
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

                // Crear el token de autenticación
                UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}