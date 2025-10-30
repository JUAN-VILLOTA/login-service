package com.couriersync.login.login_service.config;

import com.couriersync.login.login_service.Model.entity.Role;
import com.couriersync.login.login_service.Model.entity.Usuario;
import com.couriersync.login.login_service.Repository.RoleRepository;
import com.couriersync.login.login_service.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer = new DataInitializer();
    }

    @Test
    void testInitDatabaseCreatesAdminWhenNotExists() throws Exception {
        // Arrange
        Role adminRole = new Role();
        adminRole.setIdRol(1L);
        adminRole.setNombre("ADMIN");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(roleRepository.findByNombre("ADMIN")).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CommandLineRunner runner = dataInitializer.initDatabase(usuarioRepository, roleRepository, passwordEncoder);
        runner.run();

        // Assert
        verify(usuarioRepository, times(1)).findByUsername("admin");
        verify(roleRepository, times(1)).findByNombre("ADMIN");
        verify(passwordEncoder, times(1)).encode("admin123");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testInitDatabaseDoesNotCreateAdminWhenExists() throws Exception {
        // Arrange
        Usuario existingAdmin = new Usuario();
        existingAdmin.setUsername("admin");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(existingAdmin));

        // Act
        CommandLineRunner runner = dataInitializer.initDatabase(usuarioRepository, roleRepository, passwordEncoder);
        runner.run();

        // Assert
        verify(usuarioRepository, times(1)).findByUsername("admin");
        verify(roleRepository, never()).findByNombre(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testInitDatabaseThrowsExceptionWhenRoleNotFound() {
        // Arrange
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(roleRepository.findByNombre("ADMIN")).thenReturn(Optional.empty());

        // Act & Assert
        CommandLineRunner runner = dataInitializer.initDatabase(usuarioRepository, roleRepository, passwordEncoder);
        
        RuntimeException exception = assertThrows(RuntimeException.class, runner::run);
        assertTrue(exception.getMessage().contains("Rol ADMIN no encontrado"));
        
        verify(usuarioRepository, times(1)).findByUsername("admin");
        verify(roleRepository, times(1)).findByNombre("ADMIN");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testInitDatabaseBeanCreation() {
        // Act
        CommandLineRunner runner = dataInitializer.initDatabase(usuarioRepository, roleRepository, passwordEncoder);

        // Assert
        assertNotNull(runner);
    }

    @Test
    void testAdminUserHasCorrectProperties() throws Exception {
        // Arrange
        Role adminRole = new Role();
        adminRole.setIdRol(1L);
        adminRole.setNombre("ADMIN");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(roleRepository.findByNombre("ADMIN")).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword123");
        
        Usuario[] savedUser = new Usuario[1];
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            savedUser[0] = invocation.getArgument(0);
            return savedUser[0];
        });

        // Act
        CommandLineRunner runner = dataInitializer.initDatabase(usuarioRepository, roleRepository, passwordEncoder);
        runner.run();

        // Assert
        assertNotNull(savedUser[0]);
        assertEquals("admin", savedUser[0].getUsername());
        assertEquals("encodedPassword123", savedUser[0].getPassword());
        assertEquals(adminRole, savedUser[0].getRol());
    }
}
