package com.couriersync.login.login_service.Model.dto;

import java.util.List;

public class LoginResponseDTO {
    private String username;
    private String role;
    private List<String> permisos;
    private String message;

    // Constructor
    public LoginResponseDTO(String username, String role, List<String> permisos, String message) {
        this.username = username;
        this.role = role;
        this.permisos = permisos;
        this.message = message;
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public List<String> getPermisos() {
        return permisos;
    }

    public String getMessage() {
        return message;
    }
}