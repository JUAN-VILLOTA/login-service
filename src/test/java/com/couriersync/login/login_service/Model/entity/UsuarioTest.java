package com.couriersync.login.login_service.Model.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    private Usuario usuario;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setIdRol(1L);
        role.setNombre("ADMIN");
        role.setDescripcion("Administrador del sistema");

        usuario = new Usuario();
    }

    @Test
    void testUsuarioGettersAndSetters() {
        // Arrange & Act
        usuario.setIdUsuario(1L);
        usuario.setUsername("testuser");
        usuario.setPassword("password123");
        usuario.setRol(role);

        // Assert
        assertEquals(1L, usuario.getIdUsuario());
        assertEquals("testuser", usuario.getUsername());
        assertEquals("password123", usuario.getPassword());
        assertNotNull(usuario.getRol());
        assertEquals("ADMIN", usuario.getRol().getNombre());
        assertEquals(1L, usuario.getRol().getIdRol());
    }

    @Test
    void testUsuarioWithNullRole() {
        // Arrange & Act
        usuario.setIdUsuario(2L);
        usuario.setUsername("user2");
        usuario.setPassword("pass");
        usuario.setRol(null);

        // Assert
        assertNull(usuario.getRol());
    }

    @Test
    void testUsuarioInitialState() {
        // Assert
        assertNull(usuario.getIdUsuario());
        assertNull(usuario.getUsername());
        assertNull(usuario.getPassword());
        assertNull(usuario.getRol());
    }

    @Test
    void testUsuarioRoleRelationship() {
        // Arrange
        Role operadorRole = new Role();
        operadorRole.setIdRol(2L);
        operadorRole.setNombre("OPERADOR");
        
        // Act
        usuario.setIdUsuario(3L);
        usuario.setUsername("operador1");
        usuario.setPassword("operpass");
        usuario.setRol(operadorRole);

        // Assert
        assertEquals("OPERADOR", usuario.getRol().getNombre());
        assertEquals(2L, usuario.getRol().getIdRol());
    }
}
