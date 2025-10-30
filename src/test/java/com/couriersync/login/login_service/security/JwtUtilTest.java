package com.couriersync.login.login_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String testSecret = "mySecretKeyForJWTTokenGenerationAndValidation123456789";
    private final Long testExpiration = 3600000L; // 1 hora

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);
    }

    @Test
    void testGenerateToken() {
        // Arrange
        String username = "testuser";
        String role = "ADMIN";
        List<String> permisos = Arrays.asList("CREAR_USUARIO", "EDITAR_USUARIO");

        // Act
        String token = jwtUtil.generateToken(username, role, permisos);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        // Arrange
        String username = "testuser";
        String role = "ADMIN";
        List<String> permisos = Arrays.asList("CREAR_USUARIO");
        String token = jwtUtil.generateToken(username, role, permisos);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void testExtractRole() {
        // Arrange
        String username = "testuser";
        String role = "OPERADOR";
        List<String> permisos = Arrays.asList("VER_DATOS");
        String token = jwtUtil.generateToken(username, role, permisos);

        // Act
        String extractedRole = jwtUtil.extractRole(token);

        // Assert
        assertEquals(role, extractedRole);
    }

    @Test
    void testExtractPermisos() {
        // Arrange
        String username = "testuser";
        String role = "ADMIN";
        List<String> permisos = Arrays.asList("CREAR_USUARIO", "EDITAR_USUARIO", "ELIMINAR_USUARIO");
        String token = jwtUtil.generateToken(username, role, permisos);

        // Act
        List<String> extractedPermisos = jwtUtil.extractPermisos(token);

        // Assert
        assertNotNull(extractedPermisos);
        assertEquals(3, extractedPermisos.size());
        assertTrue(extractedPermisos.contains("CREAR_USUARIO"));
        assertTrue(extractedPermisos.contains("EDITAR_USUARIO"));
        assertTrue(extractedPermisos.contains("ELIMINAR_USUARIO"));
    }

    @Test
    void testExtractExpiration() {
        // Arrange
        String username = "testuser";
        String role = "ADMIN";
        List<String> permisos = Arrays.asList("CREAR_USUARIO");
        String token = jwtUtil.generateToken(username, role, permisos);

        // Act
        Date expiration = jwtUtil.extractExpiration(token);

        // Assert
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testValidateTokenSuccess() {
        // Arrange
        String username = "testuser";
        String role = "ADMIN";
        List<String> permisos = Arrays.asList("CREAR_USUARIO");
        String token = jwtUtil.generateToken(username, role, permisos);

        // Act
        Boolean isValid = jwtUtil.validateToken(token, username);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateTokenWithWrongUsername() {
        // Arrange
        String username = "testuser";
        String role = "ADMIN";
        List<String> permisos = Arrays.asList("CREAR_USUARIO");
        String token = jwtUtil.generateToken(username, role, permisos);

        // Act
        Boolean isValid = jwtUtil.validateToken(token, "wronguser");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testTokenContainsAllClaims() {
        // Arrange
        String username = "testuser";
        String role = "CONDUCTOR";
        List<String> permisos = Arrays.asList("VER_RUTAS", "ACTUALIZAR_ESTADO");
        String token = jwtUtil.generateToken(username, role, permisos);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);
        String extractedRole = jwtUtil.extractRole(token);
        List<String> extractedPermisos = jwtUtil.extractPermisos(token);

        // Assert
        assertEquals(username, extractedUsername);
        assertEquals(role, extractedRole);
        assertEquals(2, extractedPermisos.size());
        assertTrue(extractedPermisos.contains("VER_RUTAS"));
        assertTrue(extractedPermisos.contains("ACTUALIZAR_ESTADO"));
    }

    @Test
    void testGenerateTokenWithEmptyPermisos() {
        // Arrange
        String username = "testuser";
        String role = "USER";
        List<String> permisos = Arrays.asList();

        // Act
        String token = jwtUtil.generateToken(username, role, permisos);

        // Assert
        assertNotNull(token);
        List<String> extractedPermisos = jwtUtil.extractPermisos(token);
        assertNotNull(extractedPermisos);
        assertEquals(0, extractedPermisos.size());
    }

    @Test
    void testTokenExpirationIsInFuture() {
        // Arrange
        String username = "testuser";
        String role = "ADMIN";
        List<String> permisos = Arrays.asList("CREAR_USUARIO");
        String token = jwtUtil.generateToken(username, role, permisos);

        // Act
        Date expiration = jwtUtil.extractExpiration(token);
        Date now = new Date();

        // Assert
        assertTrue(expiration.after(now));
        assertTrue(expiration.getTime() - now.getTime() <= testExpiration);
    }
}
