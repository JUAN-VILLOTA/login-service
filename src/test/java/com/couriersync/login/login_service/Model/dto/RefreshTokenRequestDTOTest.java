package com.couriersync.login.login_service.Model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenRequestDTOTest {

    @Test
    void testRefreshTokenRequestDTOConstructor() {
        // Act
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("test-refresh-token");

        // Assert
        assertEquals("test-refresh-token", dto.getRefreshToken());
    }

    @Test
    void testRefreshTokenRequestDTOSettersAndGetters() {
        // Arrange
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();

        // Act
        dto.setRefreshToken("new-refresh-token");

        // Assert
        assertEquals("new-refresh-token", dto.getRefreshToken());
    }

    @Test
    void testRefreshTokenRequestDTOEmptyConstructor() {
        // Act
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();

        // Assert
        assertNull(dto.getRefreshToken());
    }
}
