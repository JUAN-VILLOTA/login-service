package com.couriersync.login.login_service.Model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestDTOTest {

    @Test
    void testSettersAndGetters() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO();
        String username = "testuser";
        String password = "testpassword";

        // Act
        dto.setUsername(username);
        dto.setPassword(password);

        // Assert
        assertEquals(username, dto.getUsername());
        assertEquals(password, dto.getPassword());
    }

    @Test
    void testDefaultConstructor() {
        // Act
        LoginRequestDTO dto = new LoginRequestDTO();

        // Assert
        assertNotNull(dto);
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
    }
}
