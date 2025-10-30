package com.couriersync.login.login_service.Model.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PermisoTest {

    private Permiso permiso;

    @BeforeEach
    void setUp() {
        permiso = new Permiso();
    }

    @Test
    void testPermisoGettersAndSetters() {
        // Arrange & Act
        permiso.setIdPermiso(1L);
        permiso.setNombre("CREAR_USUARIO");
        permiso.setDescripcion("Permite crear nuevos usuarios");

        // Assert
        assertEquals(1L, permiso.getIdPermiso());
        assertEquals("CREAR_USUARIO", permiso.getNombre());
        assertEquals("Permite crear nuevos usuarios", permiso.getDescripcion());
    }

    @Test
    void testPermisoWithRolPermisos() {
        // Arrange
        permiso.setIdPermiso(2L);
        permiso.setNombre("EDITAR_USUARIO");
        permiso.setDescripcion("Permite editar usuarios");

        List<RolPermiso> rolPermisos = new ArrayList<>();
        RolPermiso rolPermiso = new RolPermiso();
        rolPermisos.add(rolPermiso);
        
        // Act
        permiso.setRolPermisos(rolPermisos);

        // Assert
        assertNotNull(permiso.getRolPermisos());
        assertEquals(1, permiso.getRolPermisos().size());
    }

    @Test
    void testPermisoInitialState() {
        // Assert
        assertNull(permiso.getIdPermiso());
        assertNull(permiso.getNombre());
        assertNull(permiso.getDescripcion());
        assertNull(permiso.getRolPermisos());
    }

    @Test
    void testPermisoWithEmptyRolPermisos() {
        // Arrange
        permiso.setIdPermiso(3L);
        permiso.setNombre("VER_REPORTES");
        permiso.setRolPermisos(new ArrayList<>());

        // Assert
        assertNotNull(permiso.getRolPermisos());
        assertEquals(0, permiso.getRolPermisos().size());
    }

    @Test
    void testPermisoNombreUpdate() {
        // Arrange
        permiso.setNombre("OLD_PERMISSION");
        
        // Act
        permiso.setNombre("NEW_PERMISSION");

        // Assert
        assertEquals("NEW_PERMISSION", permiso.getNombre());
    }

    @Test
    void testPermisoWithNullDescription() {
        // Arrange & Act
        permiso.setIdPermiso(4L);
        permiso.setNombre("ELIMINAR_DATOS");
        permiso.setDescripcion(null);

        // Assert
        assertNull(permiso.getDescripcion());
        assertNotNull(permiso.getNombre());
    }
}
