package com.couriersync.login.login_service.Model.dto;

import java.util.List;

public class LoginResponseDTO {
    private String token;
    private String role;
    private List<String> permisos;
    private String message;

    // Constructor
    public LoginResponseDTO(String token, String role, List<String> permisos, String message) {
        this.token = token;
        this.role = role;
        this.permisos = permisos;
        this.message = message;
    }

    // Getters y Setters
    public String getToken() {
        return token;
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