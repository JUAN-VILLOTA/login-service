package com.couriersync.login.login_service.Model.dto;

public class AuthValidationDTO {
    private String username;
    private String requiredPermission;

    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRequiredPermission() { return requiredPermission; }
    public void setRequiredPermission(String requiredPermission) { this.requiredPermission = requiredPermission; }
}
