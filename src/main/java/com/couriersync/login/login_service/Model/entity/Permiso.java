package com.couriersync.login.login_service.Model.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "permisos")
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Long idPermiso;

    @Column(length = 100, nullable = false, unique = true)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    // Relación con rol_permiso
    @OneToMany(mappedBy = "permiso", cascade = CascadeType.ALL)
    private List<RolPermiso> rolPermisos;

    // Getters y Setters
    public Long getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(Long idPermiso) {
        this.idPermiso = idPermiso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<RolPermiso> getRolPermisos() {
        return rolPermisos;
    }

    public void setRolPermisos(List<RolPermiso> rolPermisos) {
        this.rolPermisos = rolPermisos;
    }
}
