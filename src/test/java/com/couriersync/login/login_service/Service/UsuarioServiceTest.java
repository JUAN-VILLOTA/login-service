package com.couriersync.login.login_service.Service;

import com.couriersync.login.login_service.Model.dto.UsuarioRequestDTO;
import com.couriersync.login.login_service.Model.entity.Role;
import com.couriersync.login.login_service.Model.entity.Usuario;
import com.couriersync.login.login_service.Repository.RoleRepository;
import com.couriersync.login.login_service.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario testUsuario;
    private Role testRole;
    private UsuarioRequestDTO usuarioRequestDTO;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setIdRol(1L);
        testRole.setNombre("ADMIN");

        testUsuario = new Usuario();
        testUsuario.setIdUsuario(1L);
        testUsuario.setUsername("testuser");
        testUsuario.setPassword("encodedPassword");
        testUsuario.setRol(testRole);

        usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setUsername("newuser");
        usuarioRequestDTO.setPassword("password123");
        usuarioRequestDTO.setIdRol(1L);
    }

    @Test
    void testGetAllUsuarios() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(testUsuario);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        List<Usuario> result = usuarioService.getAllUsuarios();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(usuarioRepository).findAll();
    }

    @Test
    void testGetUsuarioById() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(testUsuario));

        // Act
        Optional<Usuario> result = usuarioService.getUsuarioById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void testGetUsuarioByUsername() {
        // Arrange
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(testUsuario));

        // Act
        Optional<Usuario> result = usuarioService.getUsuarioByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(usuarioRepository).findByUsername("testuser");
    }

    @Test
    void testSaveUsuario() {
        // Arrange
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(testUsuario);

        // Act
        Usuario result = usuarioService.saveUsuario(testUsuario);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(usuarioRepository).save(testUsuario);
    }

    @Test
    void testCreateUsuarioSuccess() {
        // Arrange
        when(usuarioRepository.existsByUsername("newuser")).thenReturn(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(testUsuario);

        // Act
        Usuario result = usuarioService.createUsuario(usuarioRequestDTO);

        // Assert
        assertNotNull(result);
        verify(usuarioRepository).existsByUsername("newuser");
        verify(roleRepository).findById(1L);
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testCreateUsuarioUsernameAlreadyExists() {
        // Arrange
        when(usuarioRepository.existsByUsername("newuser")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.createUsuario(usuarioRequestDTO);
        });

        assertEquals("El username ya existe", exception.getMessage());
        verify(usuarioRepository).existsByUsername("newuser");
        verify(roleRepository, never()).findById(any());
    }

    @Test
    void testCreateUsuarioRoleNotFound() {
        // Arrange
        when(usuarioRepository.existsByUsername("newuser")).thenReturn(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.createUsuario(usuarioRequestDTO);
        });

        assertTrue(exception.getMessage().contains("Rol no encontrado"));
        verify(usuarioRepository).existsByUsername("newuser");
        verify(roleRepository).findById(1L);
    }

    @Test
    void testUpdateUsuarioSuccess() {
        // Arrange
        UsuarioRequestDTO updateRequest = new UsuarioRequestDTO();
        updateRequest.setUsername("updateduser");
        updateRequest.setPassword("newpassword");
        updateRequest.setIdRol(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(testUsuario));
        when(usuarioRepository.existsByUsername("updateduser")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(testUsuario);

        // Act
        Usuario result = usuarioService.updateUsuario(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(usuarioRepository).findById(1L);
        verify(passwordEncoder).encode("newpassword");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testUpdateUsuarioNotFound() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.updateUsuario(1L, usuarioRequestDTO);
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void testUpdateUsuarioUsernameAlreadyExists() {
        // Arrange
        UsuarioRequestDTO updateRequest = new UsuarioRequestDTO();
        updateRequest.setUsername("existinguser");
        updateRequest.setPassword("password");
        updateRequest.setIdRol(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(testUsuario));
        when(usuarioRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.updateUsuario(1L, updateRequest);
        });

        assertEquals("El username ya existe", exception.getMessage());
    }

    @Test
    void testUpdateUsuarioWithoutPasswordChange() {
        // Arrange
        UsuarioRequestDTO updateRequest = new UsuarioRequestDTO();
        updateRequest.setUsername("testuser");
        updateRequest.setPassword(null);
        updateRequest.setIdRol(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(testUsuario));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(testUsuario);

        // Act
        Usuario result = usuarioService.updateUsuario(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testDeleteUsuario() {
        // Arrange
        doNothing().when(usuarioRepository).deleteById(1L);

        // Act
        usuarioService.deleteUsuario(1L);

        // Assert
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void testExistsByUsername() {
        // Arrange
        when(usuarioRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean result = usuarioService.existsByUsername("testuser");

        // Assert
        assertTrue(result);
        verify(usuarioRepository).existsByUsername("testuser");
    }

    @Test
    void testNotExistsByUsername() {
        // Arrange
        when(usuarioRepository.existsByUsername("nonexistent")).thenReturn(false);

        // Act
        boolean result = usuarioService.existsByUsername("nonexistent");

        // Assert
        assertFalse(result);
        verify(usuarioRepository).existsByUsername("nonexistent");
    }
}
