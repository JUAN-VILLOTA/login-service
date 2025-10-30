package com.couriersync.login.login_service.Controller;

import com.couriersync.login.login_service.Model.dto.UsuarioRequestDTO;
import com.couriersync.login.login_service.Model.entity.Role;
import com.couriersync.login.login_service.Model.entity.Usuario;
import com.couriersync.login.login_service.Repository.UsuarioRepository;
import com.couriersync.login.login_service.Service.UsuarioService;
import com.couriersync.login.login_service.security.JwtAuthenticationFilter;
import com.couriersync.login.login_service.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

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
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsuarios() throws Exception {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(testUsuario);
        when(usuarioService.getAllUsuarios()).thenReturn(usuarios);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("testuser"));

        verify(usuarioService).getAllUsuarios();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUsuarioByIdFound() throws Exception {
        // Arrange
        when(usuarioService.getUsuarioById(1L)).thenReturn(Optional.of(testUsuario));

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.idUsuario").value(1));

        verify(usuarioService).getUsuarioById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUsuarioByIdNotFound() throws Exception {
        // Arrange
        when(usuarioService.getUsuarioById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(usuarioService).getUsuarioById(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUsuarioByUsernameFound() throws Exception {
        // Arrange
        when(usuarioService.getUsuarioByUsername("testuser")).thenReturn(Optional.of(testUsuario));

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/username/testuser")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(usuarioService).getUsuarioByUsername("testuser");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUsuarioByUsernameNotFound() throws Exception {
        // Arrange
        when(usuarioService.getUsuarioByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/username/nonexistent")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(usuarioService).getUsuarioByUsername("nonexistent");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUsuarioSuccess() throws Exception {
        // Arrange
        when(usuarioService.createUsuario(any(UsuarioRequestDTO.class))).thenReturn(testUsuario);

        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(usuarioService).createUsuario(any(UsuarioRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUsuarioFailure() throws Exception {
        // Arrange
        when(usuarioService.createUsuario(any(UsuarioRequestDTO.class)))
                .thenThrow(new RuntimeException("El username ya existe"));

        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El username ya existe"));

        verify(usuarioService).createUsuario(any(UsuarioRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUsuarioSuccess() throws Exception {
        // Arrange
        when(usuarioService.updateUsuario(eq(1L), any(UsuarioRequestDTO.class))).thenReturn(testUsuario);

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(usuarioService).updateUsuario(eq(1L), any(UsuarioRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUsuarioFailure() throws Exception {
        // Arrange
        when(usuarioService.updateUsuario(eq(999L), any(UsuarioRequestDTO.class)))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Usuario no encontrado"));

        verify(usuarioService).updateUsuario(eq(999L), any(UsuarioRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUsuarioSuccess() throws Exception {
        // Arrange
        when(usuarioService.getUsuarioById(1L)).thenReturn(Optional.of(testUsuario));
        doNothing().when(usuarioService).deleteUsuario(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/usuarios/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(usuarioService).getUsuarioById(1L);
        verify(usuarioService).deleteUsuario(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUsuarioNotFound() throws Exception {
        // Arrange
        when(usuarioService.getUsuarioById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/usuarios/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(usuarioService).getUsuarioById(999L);
        verify(usuarioService, never()).deleteUsuario(anyLong());
    }
}
