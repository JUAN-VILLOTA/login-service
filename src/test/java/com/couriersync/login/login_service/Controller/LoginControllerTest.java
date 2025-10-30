package com.couriersync.login.login_service.Controller;

import com.couriersync.login.login_service.Model.dto.*;
import com.couriersync.login.login_service.Model.entity.RefreshToken;
import com.couriersync.login.login_service.Model.entity.Role;
import com.couriersync.login.login_service.Model.entity.Usuario;
import com.couriersync.login.login_service.Service.LoginService;
import com.couriersync.login.login_service.Service.RefreshTokenService;
import com.couriersync.login.login_service.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private LoginController loginController;

    private LoginRequestDTO loginRequest;
    private LoginResponseDTO loginResponse;
    private Usuario usuario;
    private Role role;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        role = new Role();
        role.setNombre("ADMIN");
        role.setRolPermisos(new ArrayList<>());

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setUsername("testuser");
        usuario.setRol(role);

        List<String> permisos = Arrays.asList("READ", "WRITE");
        loginResponse = new LoginResponseDTO(
                "access-token-123",
                "refresh-token-456",
                "ADMIN",
                permisos,
                "Login exitoso"
        );

        refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setToken("refresh-token-456");
        refreshToken.setUsuario(usuario);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(86400));
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        when(loginService.login(any(LoginRequestDTO.class), any(HttpServletRequest.class)))
                .thenReturn(loginResponse);

        // Act
        LoginResponseDTO response = loginController.login(loginRequest, httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals("access-token-123", response.getToken());
        assertEquals("refresh-token-456", response.getRefreshToken());
        assertEquals("ADMIN", response.getRole());
        assertEquals(2, response.getPermisos().size());
        verify(loginService, times(1)).login(any(LoginRequestDTO.class), any(HttpServletRequest.class));
    }

    @Test
    void testLoginFailure() {
        // Arrange
        when(loginService.login(any(LoginRequestDTO.class), any(HttpServletRequest.class)))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loginController.login(loginRequest, httpRequest));
        verify(loginService, times(1)).login(any(LoginRequestDTO.class), any(HttpServletRequest.class));
    }

    @Test
    void testRefreshTokenSuccess() {
        // Arrange
        RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO();
        refreshRequest.setRefreshToken("refresh-token-456");

        List<String> permisos = Arrays.asList("READ", "WRITE");
        
        when(refreshTokenService.findByToken(anyString())).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(any(RefreshToken.class))).thenReturn(refreshToken);
        when(jwtUtil.generateToken(anyString(), anyString(), anyList())).thenReturn("new-access-token-789");

        // Act
        ResponseEntity<LoginResponseDTO> response = loginController.refreshToken(refreshRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("new-access-token-789", response.getBody().getToken());
        assertEquals("refresh-token-456", response.getBody().getRefreshToken());
        assertEquals("Token renovado exitosamente", response.getBody().getMessage());
        verify(refreshTokenService, times(1)).findByToken("refresh-token-456");
        verify(refreshTokenService, times(1)).verifyExpiration(any(RefreshToken.class));
        verify(jwtUtil, times(1)).generateToken(anyString(), anyString(), anyList());
    }

    @Test
    void testRefreshTokenNotFound() {
        // Arrange
        RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO();
        refreshRequest.setRefreshToken("invalid-token");

        when(refreshTokenService.findByToken(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> loginController.refreshToken(refreshRequest));
        
        assertEquals("Refresh token no encontrado", exception.getMessage());
        verify(refreshTokenService, times(1)).findByToken("invalid-token");
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyList());
    }

    @Test
    void testRefreshTokenExpired() {
        // Arrange
        RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO();
        refreshRequest.setRefreshToken("expired-token");

        when(refreshTokenService.findByToken(anyString())).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(any(RefreshToken.class)))
                .thenThrow(new RuntimeException("Refresh token expirado"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loginController.refreshToken(refreshRequest));
        verify(refreshTokenService, times(1)).findByToken("expired-token");
        verify(refreshTokenService, times(1)).verifyExpiration(any(RefreshToken.class));
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyList());
    }

    @Test
    void testLogoutSuccess() {
        // Arrange
        RefreshTokenRequestDTO logoutRequest = new RefreshTokenRequestDTO();
        logoutRequest.setRefreshToken("refresh-token-456");

        doNothing().when(refreshTokenService).revokeToken(anyString());

        // Act
        ResponseEntity<String> response = loginController.logout(logoutRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sesión cerrada exitosamente", response.getBody());
        verify(refreshTokenService, times(1)).revokeToken("refresh-token-456");
    }

    @Test
    void testLogoutWithInvalidToken() {
        // Arrange
        RefreshTokenRequestDTO logoutRequest = new RefreshTokenRequestDTO();
        logoutRequest.setRefreshToken("invalid-token");

        doThrow(new RuntimeException("Token no encontrado"))
                .when(refreshTokenService).revokeToken(anyString());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loginController.logout(logoutRequest));
        verify(refreshTokenService, times(1)).revokeToken("invalid-token");
    }

    @Test
    void testValidateTokenSuccess() {
        // Arrange
        String authHeader = "Bearer valid-token-123";
        String token = "valid-token-123";
        String username = "testuser";
        String role = "ADMIN";

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.validateToken(token, username)).thenReturn(true);
        when(jwtUtil.extractRole(token)).thenReturn(role);

        // Act
        ResponseEntity<TokenValidationDTO> response = loginController.validateToken(authHeader);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isValid());
        assertEquals(username, response.getBody().getUsername());
        assertEquals(role, response.getBody().getRole());
        verify(jwtUtil, times(1)).extractUsername(token);
        verify(jwtUtil, times(1)).validateToken(token, username);
        verify(jwtUtil, times(1)).extractRole(token);
    }

    @Test
    void testValidateTokenInvalid() {
        // Arrange
        String authHeader = "Bearer invalid-token";
        String token = "invalid-token";
        String username = "testuser";

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.validateToken(token, username)).thenReturn(false);
        when(jwtUtil.extractRole(token)).thenReturn("ADMIN");

        // Act
        ResponseEntity<TokenValidationDTO> response = loginController.validateToken(authHeader);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isValid());
        verify(jwtUtil, times(1)).extractUsername(token);
        verify(jwtUtil, times(1)).validateToken(token, username);
    }

    @Test
    void testValidateTokenWithNullHeader() {
        // Act
        ResponseEntity<TokenValidationDTO> response = loginController.validateToken(null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isValid());
        assertNull(response.getBody().getUsername());
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testValidateTokenWithEmptyHeader() {
        // Act
        ResponseEntity<TokenValidationDTO> response = loginController.validateToken("");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isValid());
        assertNull(response.getBody().getUsername());
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testValidateTokenWithBlankHeader() {
        // Act
        ResponseEntity<TokenValidationDTO> response = loginController.validateToken("   ");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isValid());
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testValidateTokenWithoutBearerPrefix() {
        // Act
        ResponseEntity<TokenValidationDTO> response = loginController.validateToken("invalid-format-token");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isValid());
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testValidateTokenThrowsException() {
        // Arrange
        String authHeader = "Bearer expired-token";
        String token = "expired-token";

        when(jwtUtil.extractUsername(token)).thenThrow(new RuntimeException("Token expirado"));

        // Act
        ResponseEntity<TokenValidationDTO> response = loginController.validateToken(authHeader);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isValid());
        assertEquals(token, response.getBody().getToken());
        assertNull(response.getBody().getUsername());
        verify(jwtUtil, times(1)).extractUsername(token);
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
    }

    @Test
    void testValidateTokenWithMalformedToken() {
        // Arrange
        String authHeader = "Bearer malformed.token";
        String token = "malformed.token";

        when(jwtUtil.extractUsername(token)).thenThrow(new IllegalArgumentException("Token malformado"));

        // Act
        ResponseEntity<TokenValidationDTO> response = loginController.validateToken(authHeader);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isValid());
        verify(jwtUtil, times(1)).extractUsername(token);
    }

    @Test
    void testRefreshTokenWithEmptyPermisos() {
        // Arrange
        RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO();
        refreshRequest.setRefreshToken("refresh-token-456");

        // Usuario sin permisos
        usuario.getRol().setRolPermisos(Collections.emptyList());

        when(refreshTokenService.findByToken(anyString())).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(any(RefreshToken.class))).thenReturn(refreshToken);
        when(jwtUtil.generateToken(anyString(), anyString(), anyList())).thenReturn("new-token");

        // Act
        ResponseEntity<LoginResponseDTO> response = loginController.refreshToken(refreshRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getPermisos().isEmpty());
        verify(jwtUtil, times(1)).generateToken(eq("testuser"), eq("ADMIN"), anyList());
    }
}
