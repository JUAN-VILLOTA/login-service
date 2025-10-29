package com.couriersync.login.login_service.Model.dto;

import java.util.List;

public class RoleDTO {
    private Long id;
    private String nombre;
    private List<String> permisos;

    public RoleDTO(Long id, String nombre, List<String> permisos) {
        this.id = id;
        this.nombre = nombre;
        this.permisos = permisos;
    }

    // Getters
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public List<String> getPermisos() { return permisos; }
}
