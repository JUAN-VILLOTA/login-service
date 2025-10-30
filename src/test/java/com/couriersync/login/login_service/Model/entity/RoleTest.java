package com.couriersync.login.login_service.Model.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
    }

    @Test
    void testRoleGettersAndSetters() {
        // Arrange & Act
        role.setIdRol(1L);
        role.setNombre("ADMIN");
        role.setDescripcion("Administrador del sistema");

        // Assert
        assertEquals(1L, role.getIdRol());
        assertEquals("ADMIN", role.getNombre());
        assertEquals("Administrador del sistema", role.getDescripcion());
    }

    @Test
    void testRoleWithRolPermisos() {
        // Arrange
        role.setIdRol(2L);
        role.setNombre("CONDUCTOR");
        role.setDescripcion("Conductor de entregas");

        List<RolPermiso> rolPermisos = new ArrayList<>();
        RolPermiso rolPermiso = new RolPermiso();
        rolPermisos.add(rolPermiso);
        
        // Act
        role.setRolPermisos(rolPermisos);

        // Assert
        assertNotNull(role.getRolPermisos());
        assertEquals(1, role.getRolPermisos().size());
    }

    @Test
    void testRoleInitialState() {
        // Assert
        assertNull(role.getIdRol());
        assertNull(role.getNombre());
        assertNull(role.getDescripcion());
        assertNull(role.getRolPermisos());
    }

    @Test
    void testRoleWithEmptyPermisos() {
        // Arrange
        role.setIdRol(3L);
        role.setNombre("OPERADOR");
        role.setRolPermisos(new ArrayList<>());

        // Assert
        assertNotNull(role.getRolPermisos());
        assertEquals(0, role.getRolPermisos().size());
    }

    @Test
    void testRoleNombreUpdate() {
        // Arrange
        role.setNombre("OLD_NAME");
        
        // Act
        role.setNombre("NEW_NAME");

        // Assert
        assertEquals("NEW_NAME", role.getNombre());
    }
}
