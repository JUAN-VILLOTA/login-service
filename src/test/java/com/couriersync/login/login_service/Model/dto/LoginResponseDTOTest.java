package com.couriersync.login.login_service.Model.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseDTOTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String token = "test-jwt-token";
        String role = "ADMIN";
        List<String> permisos = Arrays.asList("CREAR_USUARIO", "EDITAR_USUARIO");
        String message = "Login exitoso";

        // Act
        LoginResponseDTO dto = new LoginResponseDTO(token, role, permisos, message);

        // Assert
        assertEquals(token, dto.getToken());
        assertEquals(role, dto.getRole());
        assertEquals(permisos, dto.getPermisos());
        assertEquals(message, dto.getMessage());
        assertEquals(2, dto.getPermisos().size());
    }

    @Test
    void testConstructorWithEmptyPermisos() {
        // Arrange & Act
        LoginResponseDTO dto = new LoginResponseDTO(
                "token",
                "USER",
                Arrays.asList(),
                "Success"
        );

        // Assert
        assertNotNull(dto.getPermisos());
        assertEquals(0, dto.getPermisos().size());
    }

    @Test
    void testConstructorWithNullValues() {
        // Arrange & Act
        LoginResponseDTO dto = new LoginResponseDTO(null, null, null, null);

        // Assert
        assertNull(dto.getToken());
        assertNull(dto.getRole());
        assertNull(dto.getPermisos());
        assertNull(dto.getMessage());
    }
}
