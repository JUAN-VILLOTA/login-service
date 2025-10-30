package com.couriersync.login.login_service.Model.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    private RefreshToken refreshToken;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setNombre("ADMIN");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setUsername("testuser");
        usuario.setRol(role);

        refreshToken = new RefreshToken();
    }

    @Test
    void testRefreshTokenGettersAndSetters() {
        // Arrange & Act
        refreshToken.setId(1L);
        refreshToken.setToken("test-token-123");
        refreshToken.setUsuario(usuario);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600));
        refreshToken.setIpAddress("192.168.1.1");
        refreshToken.setUserAgent("Mozilla/5.0");
        refreshToken.setRevoked(false);

        // Assert
        assertEquals(1L, refreshToken.getId());
        assertEquals("test-token-123", refreshToken.getToken());
        assertEquals(usuario, refreshToken.getUsuario());
        assertNotNull(refreshToken.getExpiryDate());
        assertEquals("192.168.1.1", refreshToken.getIpAddress());
        assertEquals("Mozilla/5.0", refreshToken.getUserAgent());
        assertFalse(refreshToken.isRevoked());
    }

    @Test
    void testRefreshTokenInitialState() {
        // Assert
        assertNotNull(refreshToken.getCreatedAt());
        assertFalse(refreshToken.isRevoked());
    }

    @Test
    void testIsExpiredWhenNotExpired() {
        // Arrange
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600)); // 1 hora en el futuro

        // Act & Assert
        assertFalse(refreshToken.isExpired());
    }

    @Test
    void testIsExpiredWhenExpired() {
        // Arrange
        refreshToken.setExpiryDate(Instant.now().minusSeconds(100)); // 100 segundos en el pasado

        // Act & Assert
        assertTrue(refreshToken.isExpired());
    }

    @Test
    void testRevokedToken() {
        // Arrange & Act
        refreshToken.setRevoked(true);

        // Assert
        assertTrue(refreshToken.isRevoked());
    }

    @Test
    void testRefreshTokenWithUsuario() {
        // Arrange & Act
        refreshToken.setUsuario(usuario);

        // Assert
        assertEquals(usuario, refreshToken.getUsuario());
        assertEquals("testuser", refreshToken.getUsuario().getUsername());
    }

    @Test
    void testCreatedAtAutoSet() {
        // Arrange
        Instant before = Instant.now().minusSeconds(1);
        
        // Act
        RefreshToken newToken = new RefreshToken();
        Instant after = Instant.now().plusSeconds(1);

        // Assert
        assertNotNull(newToken.getCreatedAt());
        assertTrue(newToken.getCreatedAt().isAfter(before));
        assertTrue(newToken.getCreatedAt().isBefore(after));
    }
}
