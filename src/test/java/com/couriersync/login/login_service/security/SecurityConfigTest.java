package com.couriersync.login.login_service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "cors.allowed-origins=http://localhost:3000,http://localhost:4200"
})
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;
    
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void testPasswordEncoderBean() {
        assertNotNull(passwordEncoder);
        
        // Test encoding
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void testPasswordEncoderDoesNotMatchWrongPassword() {
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        assertFalse(passwordEncoder.matches("wrongPassword", encodedPassword));
    }

    @Test
    void testPasswordEncoderEncodesConsistently() {
        String rawPassword = "samePassword";
        String encoded1 = passwordEncoder.encode(rawPassword);
        String encoded2 = passwordEncoder.encode(rawPassword);
        
        // BCrypt generates different hashes for same password (due to salt)
        assertNotEquals(encoded1, encoded2);
        
        // But both should match the original password
        assertTrue(passwordEncoder.matches(rawPassword, encoded1));
        assertTrue(passwordEncoder.matches(rawPassword, encoded2));
    }

    @Test
    void testCorsConfigurationSourceCreation() {
        UrlBasedCorsConfigurationSource corsSource = (UrlBasedCorsConfigurationSource) securityConfig.corsConfigurationSource();
        
        assertNotNull(corsSource);
        CorsConfiguration corsConfig = corsSource.getCorsConfigurations().get("/**");
        assertNotNull(corsConfig);
        assertNotNull(corsConfig.getAllowedOrigins());
        assertNotNull(corsConfig.getAllowedMethods());
        
        // 🔒 Verificar que NO permite todos los orígenes (seguridad)
        assertFalse(corsConfig.getAllowedOrigins().contains("*"), 
            "CORS no debe permitir todos los orígenes por seguridad");
        
        // ✅ Verificar que permite los orígenes configurados
        assertTrue(corsConfig.getAllowedOrigins().contains("http://localhost:3000"));
        assertTrue(corsConfig.getAllowedOrigins().contains("http://localhost:4200"));
        
        // ✅ Verificar métodos HTTP permitidos
        assertTrue(corsConfig.getAllowedMethods().contains("GET"));
        assertTrue(corsConfig.getAllowedMethods().contains("POST"));
        assertTrue(corsConfig.getAllowedMethods().contains("PUT"));
        assertTrue(corsConfig.getAllowedMethods().contains("DELETE"));
        
        // 🔒 Verificar que allowCredentials está habilitado para orígenes específicos
        assertTrue(corsConfig.getAllowCredentials(), 
            "AllowCredentials debe ser true con orígenes específicos");
    }

    @Test
    void testPasswordEncoderCreation() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }
}
