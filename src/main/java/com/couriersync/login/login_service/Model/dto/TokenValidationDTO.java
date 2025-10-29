package com.couriersync.login.login_service.Model.dto;

public class TokenValidationDTO {
    
    private String token;
    private boolean valid;
    private String username;
    private String role;

    // Constructores
    public TokenValidationDTO() {
    }

    public TokenValidationDTO(String token, boolean valid, String username, String role) {
        this.token = token;
        this.valid = valid;
        this.username = username;
        this.role = role;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
