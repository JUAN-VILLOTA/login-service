package com.couriersync.login.login_service.Model.dto;

public class UsuarioRequestDTO {
    
    private String username;
    private String password;
    private Long idRol;

    // Constructores
    public UsuarioRequestDTO() {
    }

    public UsuarioRequestDTO(String username, String password, Long idRol) {
        this.username = username;
        this.password = password;
        this.idRol = idRol;
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }
}
