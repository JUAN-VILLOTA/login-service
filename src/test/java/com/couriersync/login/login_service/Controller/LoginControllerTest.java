package com.couriersync.login.login_service.Controller;

import com.couriersync.login.login_service.Model.dto.LoginRequestDTO;
import com.couriersync.login.login_service.Model.dto.LoginResponseDTO;
import com.couriersync.login.login_service.Service.LoginService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginService loginService;

    @MockBean
    private JwtUtil jwtUtil;

    private LoginRequestDTO loginRequest;
    private LoginResponseDTO loginResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        List<String> permisos = Arrays.asList("CREAR_USUARIO", "EDITAR_USUARIO");
        loginResponse = new LoginResponseDTO(
                "fake-jwt-token",
                "ADMIN",
                permisos,
                "Inicio de sesión exitoso"
        );
    }

    @Test
    @WithMockUser
    void testLoginSuccess() throws Exception {
        // Arrange
        when(loginService.login(any(LoginRequestDTO.class))).thenReturn(loginResponse);

        // Act & Assert
        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.permisos").isArray())
                .andExpect(jsonPath("$.permisos[0]").value("CREAR_USUARIO"))
                .andExpect(jsonPath("$.message").value("Inicio de sesión exitoso"));
    }

    @Test
    @WithMockUser
    void testLoginInvalidCredentials() throws Exception {
        // Arrange
        when(loginService.login(any(LoginRequestDTO.class)))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        // Act & Assert
        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser
    void testValidateTokenSuccess() throws Exception {
        // Arrange
        String token = "valid-jwt-token";
        when(jwtUtil.extractUsername(token)).thenReturn("admin");
        when(jwtUtil.validateToken(token, "admin")).thenReturn(true);
        when(jwtUtil.extractRole(token)).thenReturn("ADMIN");

        // Act & Assert
        mockMvc.perform(post("/api/login/validate")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser
    void testValidateTokenInvalid() throws Exception {
        // Arrange
        String token = "invalid-jwt-token";
        when(jwtUtil.extractUsername(token)).thenThrow(new RuntimeException("Invalid token"));

        // Act & Assert
        mockMvc.perform(post("/api/login/validate")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @WithMockUser
    void testValidateTokenMissingAuthorizationHeader() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/login/validate")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @WithMockUser
    void testValidateTokenInvalidAuthorizationFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/login/validate")
                        .with(csrf())
                        .header("Authorization", "InvalidFormat token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false));
    }
}
