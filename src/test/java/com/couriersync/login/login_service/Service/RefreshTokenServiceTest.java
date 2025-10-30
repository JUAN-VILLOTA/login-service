package com.couriersync.login.login_service.Service;

import com.couriersync.login.login_service.Model.entity.RefreshToken;
import com.couriersync.login.login_service.Model.entity.Role;
import com.couriersync.login.login_service.Model.entity.Usuario;
import com.couriersync.login.login_service.Repository.RefreshTokenRepository;
import com.couriersync.login.login_service.Repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private Usuario usuario;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        // Configurar duración del refresh token (7 días en ms)
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 604800000L);

        Role role = new Role();
        role.setNombre("ADMIN");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setUsername("testuser");
        usuario.setPassword("encodedPassword");
        usuario.setRol(role);

        refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setToken("test-refresh-token");
        refreshToken.setUsuario(usuario);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)); // 7 días
        refreshToken.setRevoked(false);
    }

    @Test
    void testCreateRefreshToken() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RefreshToken result = refreshTokenService.createRefreshToken(1L, httpServletRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals(usuario, result.getUsuario());
        assertEquals("127.0.0.1", result.getIpAddress());
        assertEquals("Mozilla/5.0", result.getUserAgent());
        verify(refreshTokenRepository, times(1)).revokeAllByUsuario(usuario);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void testCreateRefreshTokenWithoutRequest() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RefreshToken result = refreshTokenService.createRefreshToken(1L, null);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertNull(result.getIpAddress());
        assertNull(result.getUserAgent());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void testCreateRefreshTokenUserNotFound() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> refreshTokenService.createRefreshToken(999L, httpServletRequest));
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void testFindByToken() {
        // Arrange
        when(refreshTokenRepository.findByToken("test-refresh-token")).thenReturn(Optional.of(refreshToken));

        // Act
        Optional<RefreshToken> result = refreshTokenService.findByToken("test-refresh-token");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test-refresh-token", result.get().getToken());
        verify(refreshTokenRepository, times(1)).findByToken("test-refresh-token");
    }

    @Test
    void testVerifyExpirationValid() {
        // Act
        RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);

        // Assert
        assertNotNull(result);
        assertEquals(refreshToken.getToken(), result.getToken());
    }

    @Test
    void testVerifyExpirationExpired() {
        // Arrange
        refreshToken.setExpiryDate(Instant.now().minusSeconds(100)); // Token expirado

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> refreshTokenService.verifyExpiration(refreshToken));
        
        assertTrue(exception.getMessage().contains("expirado"));
        verify(refreshTokenRepository, times(1)).delete(refreshToken);
    }

    @Test
    void testVerifyExpirationRevoked() {
        // Arrange
        refreshToken.setRevoked(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> refreshTokenService.verifyExpiration(refreshToken));
        
        assertTrue(exception.getMessage().contains("revocado"));
        verify(refreshTokenRepository, never()).delete(refreshToken);
    }

    @Test
    void testRevokeToken() {
        // Arrange
        when(refreshTokenRepository.findByToken("test-refresh-token")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        refreshTokenService.revokeToken("test-refresh-token");

        // Assert
        verify(refreshTokenRepository, times(1)).findByToken("test-refresh-token");
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void testRevokeTokenNotFound() {
        // Arrange
        when(refreshTokenRepository.findByToken("non-existent-token")).thenReturn(Optional.empty());

        // Act
        refreshTokenService.revokeToken("non-existent-token");

        // Assert
        verify(refreshTokenRepository, times(1)).findByToken("non-existent-token");
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void testRevokeUserTokens() {
        // Act
        refreshTokenService.revokeUserTokens(usuario);

        // Assert
        verify(refreshTokenRepository, times(1)).revokeAllByUsuario(usuario);
    }

    @Test
    void testDeleteExpiredTokens() {
        // Arrange
        RefreshToken expiredToken = new RefreshToken();
        expiredToken.setExpiryDate(Instant.now().minusSeconds(100));
        
        when(refreshTokenRepository.findAll()).thenReturn(java.util.List.of(expiredToken, refreshToken));

        // Act
        int deleted = refreshTokenService.deleteExpiredTokens();

        // Assert
        assertEquals(1, deleted);
        verify(refreshTokenRepository, times(1)).delete(expiredToken);
        verify(refreshTokenRepository, never()).delete(refreshToken);
    }
}
