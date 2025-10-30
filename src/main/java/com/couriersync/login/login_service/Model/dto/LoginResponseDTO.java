package com.couriersync.login.login_service.Model.dto;

import java.util.List;

public class LoginResponseDTO {
    private String token;
    private String refreshToken;
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

    // Constructor con refresh token
    public LoginResponseDTO(String token, String refreshToken, String role, List<String> permisos, String message) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.role = role;
        this.permisos = permisos;
        this.message = message;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<String> permisos) {
        this.permisos = permisos;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}