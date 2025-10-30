package com.couriersync.login.login_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configura la cadena de filtros de seguridad para la aplicación.
     * 
     * CSRF está deshabilitado intencionalmente porque:
     * 1. Esta es una API REST stateless que usa JWT para autenticación
     * 2. No maneja sesiones de usuario (SessionCreationPolicy.STATELESS)
     * 3. Los tokens JWT no son vulnerables a CSRF ya que se envían en headers, no en cookies
     * 4. Los clientes de esta API son aplicaciones móviles/web que manejan tokens explícitamente
     * 
     * Para APIs REST con JWT, CSRF protection no es necesaria y puede causar problemas.
     * Ver: https://spring.io/blog/2013/08/21/spring-security-3-2-0-rc1-highlights-csrf-protection/
     */
    @Bean
    @SuppressWarnings("java:S4502") // CSRF protection is intentionally disabled for stateless JWT API
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ✅ Habilitar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 🚫 Desactivar CSRF - Seguro para APIs REST stateless con JWT
            // No usamos cookies de sesión, por lo tanto CSRF no aplica
            .csrf(csrf -> csrf.disable())

            // ⚙️ Configurar reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // ✅ Permitir login sin autenticación
                .requestMatchers("/api/login/**").permitAll()

                // 🔒 Solo ADMIN puede crear, actualizar y eliminar usuarios
                .requestMatchers(HttpMethod.POST, "/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                
                // ✅ Cualquier usuario autenticado puede consultar usuarios
                .requestMatchers(HttpMethod.GET, "/api/usuarios/**").authenticated()

                // 🔒 Solo ADMIN puede gestionar roles y permisos
                .requestMatchers("/api/roles/**", "/api/permisos/**").hasRole("ADMIN")

                // ✅ Permitir acceso libre a Swagger
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                // ✅ Permitir el health check de Spring Actuator
                .requestMatchers("/actuator/health").permitAll()

                // 🔒 Todo lo demás requiere autenticación JWT
                .anyRequest().authenticated()
            )

            // 🚫 Sin manejo de sesiones (stateless → JWT)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🧩 Agregar el filtro JWT antes del filtro estándar de Spring
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ Configuración de CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Permitir todos los orígenes
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(false); // Debe ser false cuando origins es "*"
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Codificador de contraseñas con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}