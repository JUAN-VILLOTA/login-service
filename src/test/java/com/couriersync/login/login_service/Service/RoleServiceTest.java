package com.couriersync.login.login_service.Service;

import com.couriersync.login.login_service.Model.entity.Role;
import com.couriersync.login.login_service.Repository.RoleRepository;
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
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setIdRol(1L);
        testRole.setNombre("ADMIN");
    }

    @Test
    void testGetAllRoles() {
        // Arrange
        Role role2 = new Role();
        role2.setIdRol(2L);
        role2.setNombre("OPERADOR");

        List<Role> roles = Arrays.asList(testRole, role2);
        when(roleRepository.findAll()).thenReturn(roles);

        // Act
        List<Role> result = roleService.getAllRoles();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ADMIN", result.get(0).getNombre());
        assertEquals("OPERADOR", result.get(1).getNombre());
        verify(roleRepository).findAll();
    }

    @Test
    void testGetRoleById() {
        // Arrange
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));

        // Act
        Optional<Role> result = roleService.getRoleById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getNombre());
        assertEquals(1L, result.get().getIdRol());
        verify(roleRepository).findById(1L);
    }

    @Test
    void testGetRoleByIdNotFound() {
        // Arrange
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Role> result = roleService.getRoleById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(roleRepository).findById(999L);
    }

    @Test
    void testSaveRole() {
        // Arrange
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // Act
        Role result = roleService.saveRole(testRole);

        // Assert
        assertNotNull(result);
        assertEquals("ADMIN", result.getNombre());
        assertEquals(1L, result.getIdRol());
        verify(roleRepository).save(testRole);
    }

    @Test
    void testSaveNewRole() {
        // Arrange
        Role newRole = new Role();
        newRole.setNombre("CONDUCTOR");

        Role savedRole = new Role();
        savedRole.setIdRol(3L);
        savedRole.setNombre("CONDUCTOR");

        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);

        // Act
        Role result = roleService.saveRole(newRole);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getIdRol());
        assertEquals("CONDUCTOR", result.getNombre());
        verify(roleRepository).save(newRole);
    }

    @Test
    void testDeleteRole() {
        // Arrange
        doNothing().when(roleRepository).deleteById(1L);

        // Act
        roleService.deleteRole(1L);

        // Assert
        verify(roleRepository).deleteById(1L);
    }

    @Test
    void testGetAllRolesEmpty() {
        // Arrange
        when(roleRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Role> result = roleService.getAllRoles();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(roleRepository).findAll();
    }
}
