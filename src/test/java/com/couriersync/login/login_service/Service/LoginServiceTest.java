package com.couriersync.login.login_service.Service;

import com.couriersync.login.login_service.Model.dto.LoginRequestDTO;
import com.couriersync.login.login_service.Model.dto.LoginResponseDTO;
import com.couriersync.login.login_service.Model.entity.Permiso;
import com.couriersync.login.login_service.Model.entity.RefreshToken;
import com.couriersync.login.login_service.Model.entity.Role;
import com.couriersync.login.login_service.Model.entity.Usuario;
import com.couriersync.login.login_service.Repository.PermisoRepository;
import com.couriersync.login.login_service.Repository.RolePermisoRepository;
import com.couriersync.login.login_service.Repository.RoleRepository;
import com.couriersync.login.login_service.Repository.UsuarioRepository;
import com.couriersync.login.login_service.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RolePermisoRepository rolePermisoRepository;

    @Mock
    private PermisoRepository permisoRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private LoginService loginService;

    private Usuario testUsuario;
    private Role testRole;
    private Permiso testPermiso;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setIdRol(1L);
        testRole.setNombre("ADMIN");

        testUsuario = new Usuario();
        testUsuario.setIdUsuario(1L);
        testUsuario.setUsername("admin");
        testUsuario.setPassword("$2a$10$encodedPassword");
        testUsuario.setRol(testRole);

        testPermiso = new Permiso();
        testPermiso.setIdPermiso(1L);
        testPermiso.setNombre("CREAR_USUARIO");

        refreshToken = new RefreshToken();
        refreshToken.setToken("test-refresh-token");
        refreshToken.setUsuario(testUsuario);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60));
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin");
        request.setPassword("admin123");

        List<Long> permisosIds = Arrays.asList(1L, 2L);
        List<String> permisosNombres = Arrays.asList("CREAR_USUARIO", "EDITAR_USUARIO");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(testUsuario));
        when(passwordEncoder.matches("admin123", testUsuario.getPassword())).thenReturn(true);
        when(rolePermisoRepository.findPermisosByRolId(1L)).thenReturn(permisosIds);
        when(permisoRepository.findById(1L)).thenReturn(Optional.of(testPermiso));
        
        Permiso permiso2 = new Permiso();
        permiso2.setIdPermiso(2L);
        permiso2.setNombre("EDITAR_USUARIO");
        when(permisoRepository.findById(2L)).thenReturn(Optional.of(permiso2));
        
        when(jwtUtil.generateToken(anyString(), anyString(), any())).thenReturn("fake-jwt-token");
        when(refreshTokenService.createRefreshToken(any(), any())).thenReturn(refreshToken);

        // Act
        LoginResponseDTO response = loginService.login(request, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("test-refresh-token", response.getRefreshToken());
        assertEquals("ADMIN", response.getRole());
        assertEquals(2, response.getPermisos().size());
        assertEquals("Inicio de sesión exitoso", response.getMessage());

        verify(usuarioRepository).findByUsername("admin");
        verify(passwordEncoder).matches("admin123", testUsuario.getPassword());
        verify(jwtUtil).generateToken(anyString(), anyString(), any());
        verify(refreshTokenService).createRefreshToken(any(), any());
    }

    @Test
    void testLoginUserNotFound() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("noexiste");
        request.setPassword("password");

        when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginService.login(request, httpServletRequest);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(usuarioRepository).findByUsername("noexiste");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testLoginInvalidPassword() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin");
        request.setPassword("wrongpassword");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(testUsuario));
        when(passwordEncoder.matches("wrongpassword", testUsuario.getPassword())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginService.login(request, httpServletRequest);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(usuarioRepository).findByUsername("admin");
        verify(passwordEncoder).matches("wrongpassword", testUsuario.getPassword());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), any());
    }

    @Test
    void testLoginWithEmptyPermisos() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin");
        request.setPassword("admin123");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(testUsuario));
        when(passwordEncoder.matches("admin123", testUsuario.getPassword())).thenReturn(true);
        when(rolePermisoRepository.findPermisosByRolId(1L)).thenReturn(Arrays.asList());
        when(jwtUtil.generateToken(anyString(), anyString(), any())).thenReturn("fake-jwt-token");
        when(refreshTokenService.createRefreshToken(any(), any())).thenReturn(refreshToken);

        // Act
        LoginResponseDTO response = loginService.login(request, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getPermisos().size());
    }
}
