package com.couriersync.login.login_service.Service;

import com.couriersync.login.login_service.Model.entity.Permiso;
import com.couriersync.login.login_service.Repository.PermisoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermisoServiceTest {

    @Mock
    private PermisoRepository permisoRepository;

    @InjectMocks
    private PermisoService permisoService;

    private Permiso testPermiso;

    @BeforeEach
    void setUp() {
        testPermiso = new Permiso();
        testPermiso.setIdPermiso(1L);
        testPermiso.setNombre("CREAR_USUARIO");
    }

    @Test
    void testGetAllPermisos() {
        // Arrange
        Permiso permiso2 = new Permiso();
        permiso2.setIdPermiso(2L);
        permiso2.setNombre("EDITAR_USUARIO");

        List<Permiso> permisos = Arrays.asList(testPermiso, permiso2);
        when(permisoRepository.findAll()).thenReturn(permisos);

        // Act
        List<Permiso> result = permisoService.getAllPermisos();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("CREAR_USUARIO", result.get(0).getNombre());
        assertEquals("EDITAR_USUARIO", result.get(1).getNombre());
        verify(permisoRepository).findAll();
    }

    @Test
    void testGetPermisoById() {
        // Arrange
        when(permisoRepository.findById(1L)).thenReturn(Optional.of(testPermiso));

        // Act
        Optional<Permiso> result = permisoService.getPermisoById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("CREAR_USUARIO", result.get().getNombre());
        assertEquals(1L, result.get().getIdPermiso());
        verify(permisoRepository).findById(1L);
    }

    @Test
    void testGetPermisoByIdNotFound() {
        // Arrange
        when(permisoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Permiso> result = permisoService.getPermisoById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(permisoRepository).findById(999L);
    }

    @Test
    void testSavePermiso() {
        // Arrange
        when(permisoRepository.save(any(Permiso.class))).thenReturn(testPermiso);

        // Act
        Permiso result = permisoService.savePermiso(testPermiso);

        // Assert
        assertNotNull(result);
        assertEquals("CREAR_USUARIO", result.getNombre());
        assertEquals(1L, result.getIdPermiso());
        verify(permisoRepository).save(testPermiso);
    }

    @Test
    void testSaveNewPermiso() {
        // Arrange
        Permiso newPermiso = new Permiso();
        newPermiso.setNombre("ELIMINAR_USUARIO");

        Permiso savedPermiso = new Permiso();
        savedPermiso.setIdPermiso(3L);
        savedPermiso.setNombre("ELIMINAR_USUARIO");

        when(permisoRepository.save(any(Permiso.class))).thenReturn(savedPermiso);

        // Act
        Permiso result = permisoService.savePermiso(newPermiso);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getIdPermiso());
        assertEquals("ELIMINAR_USUARIO", result.getNombre());
        verify(permisoRepository).save(newPermiso);
    }

    @Test
    void testDeletePermiso() {
        // Arrange
        doNothing().when(permisoRepository).deleteById(1L);

        // Act
        permisoService.deletePermiso(1L);

        // Assert
        verify(permisoRepository).deleteById(1L);
    }

    @Test
    void testGetAllPermisosEmpty() {
        // Arrange
        when(permisoRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Permiso> result = permisoService.getAllPermisos();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(permisoRepository).findAll();
    }

    @Test
    void testSaveMultiplePermisos() {
        // Arrange
        Permiso permiso1 = new Permiso();
        permiso1.setNombre("VER_DATOS");
        
        Permiso permiso2 = new Permiso();
        permiso2.setNombre("MODIFICAR_DATOS");

        when(permisoRepository.save(any(Permiso.class)))
                .thenReturn(permiso1)
                .thenReturn(permiso2);

        // Act
        Permiso result1 = permisoService.savePermiso(permiso1);
        Permiso result2 = permisoService.savePermiso(permiso2);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("VER_DATOS", result1.getNombre());
        assertEquals("MODIFICAR_DATOS", result2.getNombre());
        verify(permisoRepository, times(2)).save(any(Permiso.class));
    }
}
