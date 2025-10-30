package com.couriersync.login.login_service.Model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioRequestDTOTest {

    @Test
    void testSettersAndGetters() {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        String username = "newuser";
        String password = "password123";
        Long idRol = 1L;

        // Act
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setIdRol(idRol);

        // Assert
        assertEquals(username, dto.getUsername());
        assertEquals(password, dto.getPassword());
        assertEquals(idRol, dto.getIdRol());
    }

    @Test
    void testDefaultConstructor() {
        // Act
        UsuarioRequestDTO dto = new UsuarioRequestDTO();

        // Assert
        assertNotNull(dto);
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
        assertNull(dto.getIdRol());
    }

    @Test
    void testWithNullPassword() {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        
        // Act
        dto.setUsername("testuser");
        dto.setPassword(null);
        dto.setIdRol(2L);

        // Assert
        assertEquals("testuser", dto.getUsername());
        assertNull(dto.getPassword());
        assertEquals(2L, dto.getIdRol());
    }
}
